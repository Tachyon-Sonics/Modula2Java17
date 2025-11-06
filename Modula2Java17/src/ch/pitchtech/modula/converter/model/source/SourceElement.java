package ch.pitchtech.modula.converter.model.source;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import ch.pitchtech.modula.converter.compiler.SourceFile;
import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.NodeList;

public class SourceElement implements INode {
    
    private final SourceFile sourceFile;
    private final SourceLocation sourceLocation;
    private INode parent;
    private final List<INode> children = new ArrayList<>();
    private final Map<NodeAttachType, List<INode>> attachMap = new EnumMap<>(NodeAttachType.class);
    private String newName;
    
    
    public SourceElement(SourceLocation sourceLocation) {
        this.sourceFile = CurrentFile.getCurrentFile();
        this.sourceLocation = sourceLocation;
    }

    public static SourceLocation locationOf(ParseTree node) {
        while (node.getParent() != null && !(node instanceof ParserRuleContext))
            node = node.getParent();
        if (node instanceof ParserRuleContext prc) {
            return new SourceLocation(
                    prc.getStart().getLine(),
                    prc.getStart().getCharPositionInLine(),
                    prc.getStop().getLine(),
                    prc.getStop().getCharPositionInLine() + prc.getStop().getText().length() - 1
            );
        }
        return new SourceLocation(0, 0, -1, -1);
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }
    
    protected void attach(INode child, NodeAttachType type) {
        attachTo(this, child);
        attachMap.computeIfAbsent(type, (k) -> new ArrayList<>()).add(child);
    }
    
    private static void attachTo(INode parent, INode child) {
        if (child.getParentNode() != null)
            child.getParentNode().getChildren().remove(child);
        child.setParentNode(parent);
        parent.getChildren().add(child);
    }
    
    protected <T extends INode> void attach(List<T> children, NodeAttachType type) {
        NodeList<T> nodeList = new NodeList<>(this, children, type);
        getChildren().add(nodeList);
        for (INode child : children) {
            attachTo(nodeList, child);
        }
        attachMap.computeIfAbsent(type, (k) -> new ArrayList<>()).add(nodeList);
    }
    
    @Override
    public INode getParentNode() {
        return parent;
    }

    @Override
    public void setParentNode(INode parent) {
        this.parent = parent;
    }

    @Override
    public List<INode> getChildren() {
        return children;
    }

    @Override
    public NodeAttachType getAttachType(INode child) {
        for (Map.Entry<NodeAttachType, List<INode>> entry : attachMap.entrySet()) {
            if (entry.getValue().contains(child))
                return entry.getKey();
        }
        return NodeAttachType.DEFAULT;
    }
    
    /**
     * If not <tt>null</tt>, the new name to use in the generated Java code
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Set the new name to use in the generated Java code. If <tt>null</tt> (default), the actual Modula-2
     * name is used unmodified
     */
    public void setNewName(String newName) {
        this.newName = newName;
    }

    public SourceLocation getSourceLocation() {
        return sourceLocation;
    }
    
    public SourceInfo getSourceInfo() {
        return new SourceInfo(sourceFile.toString(), sourceLocation);
    }

    public String getLocationInfo() {
        String result = "file '" + sourceFile.toString() + "'";
        if (sourceLocation.startLine() >= sourceLocation.stopLine())
            result += " at " + sourceLocation.startLine() + ":" + sourceLocation.startColumn();
        else
            result += " from line " + sourceLocation.startLine() + " to " + sourceLocation.stopLine();
        return result;
    }
    
    /**
     * Get the Modula-2 source code of this element. For debugging purposes
     */
    public String getSourceCode() {
        try {
            String source = Files.readString(sourceFile.getPath());
            String[] lines = source.split("[\r]?\n");
            StringBuilder result = new StringBuilder();
            for (int lineNum = sourceLocation.startLine(); lineNum <= sourceLocation.stopLine(); lineNum++) {
                String line = lines[lineNum - 1];
                if (lineNum == sourceLocation.startLine() && lineNum == sourceLocation.stopLine()) {
                    line = line.substring(sourceLocation.startColumn(), sourceLocation.stopColumn() + 1);
                } else if (lineNum == sourceLocation.startLine()) {
                    line = line.substring(sourceLocation.startColumn());
                } else if (lineNum == sourceLocation.stopLine()) {
                    line = line.substring(0, sourceLocation.stopColumn() + 1);
                }
                if (result.length() > 0)
                    result.append("\n");
                result.append(line);
            }
            return result.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
