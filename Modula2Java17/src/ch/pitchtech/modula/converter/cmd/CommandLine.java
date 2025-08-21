package ch.pitchtech.modula.converter.cmd;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.FileOptions;
import ch.pitchtech.modula.converter.compiler.SourceFile;

public class CommandLine {
    
    private final FileOptions fileOptions = new FileOptions();
    private final CompilerOptions compilerOptions = new CompilerOptions();
    private final List<SourceFile> filesToCompile = new ArrayList<>();

    
    public CommandLine() {
        // Fill defaults
        fileOptions.setTargetMainDir(new File("").toPath());
        fileOptions.setTargetLibraryDir(new File("").toPath());
        compilerOptions.setTargetPackageMain("org.modula2.generated");
        compilerOptions.setTargetPackageLib("org.modula2.generated.library");
    }
    
    /**
     * Parse the given command line. After this method is invoked, {@link #getFileOptions()},
     * {@link #getCompilerOptions()} and {@link #getFilesToCompile()} can be retrieved and passed
     * to a {@link Compiler}.
     * @param args command-line arguments
     * @return <tt>null</tt> on success, the error message else
     */
    public String parse(String[] args) {
        // Check for help
        if (args.length == 0) {
            displayShortHelp();
            System.exit(1);
        } else if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            displayHelp();
            System.exit(0);
        }
        
        // Parse arguments
        Set<CmdOption> specifiedOptions = new HashSet<>();
        int index = 0;
        while (index < args.length) {
            String arg = args[index];
            String valueStr = null;
            if (arg.startsWith("-") || arg.startsWith("--")) {
                boolean full = (arg.startsWith("--"));
                if (full && arg.contains("=")) { // "option=value" syntax
                    int ePos = arg.indexOf("=");
                    valueStr = arg.substring(ePos + "=".length());
                    arg = arg.substring(0, ePos);
                }
                CmdOption option;
                if (full)
                    option = CmdOption.parseFull(CmdOptions.getAllOptions(), arg.substring(2));
                else
                    option = CmdOption.parse(CmdOptions.getAllOptions(), arg.substring(1));
                if (option == null) {
                    return "Unknown option: " + arg;
                }
                specifiedOptions.add(option);
                Object value = null;
                if (option.isHasValue()) {
                    if (valueStr == null) {
                        index++;
                        if (index >= args.length) {
                            return "Value of type '" + option.getValueType().name().toLowerCase() + "' expected after option: " + arg;
                        }
                        valueStr = args[index];
                    }
                    value = option.parseValue(valueStr);
                    if (value instanceof String) {
                        boolean isOption = false;
                        for (CmdOption o : CmdOptions.getAllOptions()) {
                            if (valueStr.equals("-" + o.getName()) || valueStr.equals("--" + o.getFullName())) {
                                isOption = true;
                            }
                        }
                        if (isOption) {
                            return "Value of type '" + option.getValueType().name().toLowerCase() + "' expected after option: " + arg;
                        }
                        if (option.getValueType() != OptionType.STRING) {
                            String error = (String) value;
                            return arg + " " + valueStr + ": " + error;
                        }
                    }
                } else if (valueStr != null) {
                    return "No value expected for option: " + arg;
                }

                // Apply option
                option.apply(value, fileOptions, compilerOptions);
            } else {
                // Input file
                Path inputFile = Path.of(arg);
                if (!Files.exists(inputFile))
                    return "File not found: " + arg;
                else if (!Files.isRegularFile(inputFile))
                    return "'" + arg + "' is not a file";
                filesToCompile.add(new SourceFile(inputFile));
            }
            index++;
        }
        
        // Error if not input file is specified
        if (filesToCompile.isEmpty()) {
            displayShortHelp();
            System.exit(1);
        }
        
        // Warn missing important stuff
        if (!specifiedOptions.contains(CmdOptions.TARGET_MAIN_DIR)) {
            System.out.println("WARNING: The Java code will be generated in the current directory."
                    + " Use the  --" + CmdOptions.TARGET_MAIN_DIR.getFullName() + "  option to change it.");
        }
        if (!specifiedOptions.contains(CmdOptions.TARGET_PACKAGE_MAIN)) {
            System.out.println("WARNING: The generated Java code will use the \""
                    + compilerOptions.getTargetPackageMain() + "\" package."
                    + " Use the  --" + CmdOptions.TARGET_PACKAGE_MAIN.getFullName() + "  option to change it.");
        }
        
        // Resolve defaults
        if (specifiedOptions.contains(CmdOptions.TARGET_MAIN_DIR) 
                && !specifiedOptions.contains(CmdOptions.TARGET_LIBRARY_DIR)) {
            fileOptions.setTargetLibraryDir(fileOptions.getTargetMainDir());
        }
        if (specifiedOptions.contains(CmdOptions.TARGET_PACKAGE_MAIN)
                && !specifiedOptions.contains(CmdOptions.TARGET_PACKAGE_LIB)) {
            compilerOptions.setTargetPackageLib(compilerOptions.getTargetPackageMain());
        }
        
        return null; // Success
    }

    public FileOptions getFileOptions() {
        return fileOptions;
    }

    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }
    
    public List<SourceFile> getFilesToCompile() {
        return filesToCompile;
    }
    
    private void displayShortHelp() {
        String appCommandLine = getAppCommandLine();
        System.out.println("No input file(s) specified.");
        System.out.println();
        System.out.println("Usage: " + appCommandLine + " <input file>.mod -o <output directory> -p <java package>");
        System.out.println("Example: " + appCommandLine + " MainModule.mod -o ./src/java -p org.example.generated");
        System.out.println("  Only the main MODULE must be specified; dependencies will be compiled automatically.");
        System.out.println("To show all available options: " + appCommandLine + " -h");
    }
    
    private void displayHelp() {
        String appCommandLine = getAppCommandLine();
        System.out.println("Usage: " + appCommandLine + " <input file> [<options>...]");
        System.out.println();
        System.out.println("<input file> must be a MODULE file. All required IMPLEMENTATION and DEFINITION modules will be compiled automatically");
        System.out.println();
        System.out.println("Options:");
        for (CmdOption option : CmdOptions.getAllOptions()) {
            System.out.print(" -");
            System.out.print(option.getName());
            System.out.print(", --");
            System.out.print(option.getFullName());
            if (option.getArgName() != null)
                System.out.print(" " + option.getArgName());
            System.out.print(": " + option.getDescription());
            System.out.println();
        }
        System.out.println();
        System.out.println("Example:");
        System.out.println("  " + appCommandLine + " MyModule.mod --output src/java --package org.example");
        System.out.println("    Compiles \"MyModule.mod\" and all files it imports directly or indirectly, write the resulting Java files in the \"src/java\" directory, inside the \"org.example\" package");
    }

    private String getAppCommandLine() {
        String appCommandLine = System.getProperty("jpackage.app-path");
        if (appCommandLine == null) {
            appCommandLine = "java -jar Modula2Java17.jar";
        } else {
            int lastSep = appCommandLine.lastIndexOf(File.separatorChar);
            appCommandLine = appCommandLine.substring(lastSep + 1);
        }
        return appCommandLine;
    }
    
    public static void main(String[] args) {
        new CommandLine().displayHelp();
        System.out.println("\n");
        new CommandLine().parse(args);
    }

}
