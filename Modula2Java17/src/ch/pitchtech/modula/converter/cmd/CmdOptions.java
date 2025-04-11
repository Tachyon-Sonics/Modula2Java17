package ch.pitchtech.modula.converter.cmd;

import java.nio.file.Path;
import java.util.List;

import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.FileOptions;

public class CmdOptions {

    public final static CmdOption TARGET_MAIN_DIR = new CmdOption("o", "output", OptionType.DIRECTORY,
            "<directory>", "Output directory for generated Java code") {

        @Override
        public void apply(Object value, FileOptions fileOptions, CompilerOptions compilerOptions) {
            Path outputDir = (Path) value;
            fileOptions.setTargetMainDir(outputDir);
        }
    };

    public final static CmdOption TARGET_LIBRARY_DIR = new CmdOption("ol", "output-library", OptionType.DIRECTORY,
            "<directory>", "Output directory for generated Java library code") {

        @Override
        public void apply(Object value, FileOptions fileOptions, CompilerOptions compilerOptions) {
            Path outputDir = (Path) value;
            fileOptions.setTargetLibraryDir(outputDir);
        }

    };
    
    public final static CmdOption TARGET_PACKAGE_MAIN = new CmdOption("p", "package", OptionType.STRING,
            "<java package>", "Target Java package for generated code") {

        @Override
        public void apply(Object value, FileOptions fileOptions, CompilerOptions compilerOptions) {
            String pck = (String) value;
            compilerOptions.setTargetPackageMain(pck);
        }
        
    };

    public final static CmdOption TARGET_PACKAGE_LIB = new CmdOption("pl", "package-library", OptionType.STRING,
            "<java package>", "Target Java package for generated library code") {

        @Override
        public void apply(Object value, FileOptions fileOptions, CompilerOptions compilerOptions) {
            String pck = (String) value;
            compilerOptions.setTargetPackageLib(pck);
        }
        
    };
    
    public final static List<CmdOption> getAllOptions() {
        return List.of(TARGET_MAIN_DIR, TARGET_LIBRARY_DIR, TARGET_PACKAGE_MAIN, TARGET_PACKAGE_LIB);
    }

}
