package ch.pitchtech.modula.converter.cmd;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;

import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.DataModelType;
import ch.pitchtech.modula.converter.compiler.FileOptions;
import ch.pitchtech.modula.converter.utils.Logger;

public class CmdOptions {
    
    public final static CmdOption SOURCE_DIR = new CmdOption("s", "source", OptionType.DIRECTORY,
            "<directory>", "Additional source directory (can be specified multiple times)") {
        
        @Override
        public void apply(Object value, FileOptions fileOptions, CompilerOptions compilerOptions) {
            Path inputDir = (Path) value;
            fileOptions.addM2sourceDir(inputDir);
        }
    };

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
    
    public final static CmdOption TEXT_ENCODING = new CmdOption("te", "text-encoding", OptionType.STRING,
            "<encoding>", "Source files text encoding such as ISO-8859-1 (default UTF-8)") {

        @Override
        public void apply(Object value, FileOptions fileOptions, CompilerOptions compilerOptions) {
            String charsetName = (String) value;
            Charset charset = Charset.forName(charsetName);
            compilerOptions.setCharset(charset);
        }
        
    };
    
    private static String getDataModelValues() {
        StringJoiner joiner = new StringJoiner("|");
        for (DataModelType dataModel : DataModelType.values()) {
            joiner.add(dataModel.name());
        }
        return joiner.toString();
    }
    
    public final static CmdOption DATA_MODEL = new CmdOption("dm", "data-model", DataModelType.class,
            getDataModelValues(), "Size of INTEGER (16 or 32 bit - default 16 bit) and related types") {

        @Override
        public void apply(Object value, FileOptions fileOptions, CompilerOptions compilerOptions) {
            DataModelType dataModel = (DataModelType) value;
            compilerOptions.setDataModel(dataModel);
        }
        
    };
    
    public final static CmdOption VERBOSE = new CmdOption("v", "verbose", OptionType.INTEGER,
            "<level>", "Set verbose level between 0 and 2") {
        
        @Override
        public void apply(Object value, FileOptions fileOptions, CompilerOptions compilerOptions) {
            int verboseLevel = (int) value;
            Logger.setVerboseLevel(verboseLevel);
        }
    };
    
    public final static List<CmdOption> getAllOptions() {
        return List.of(SOURCE_DIR, TARGET_MAIN_DIR, TARGET_LIBRARY_DIR, TARGET_PACKAGE_MAIN, TARGET_PACKAGE_LIB,
                DATA_MODEL, VERBOSE);
    }

}
