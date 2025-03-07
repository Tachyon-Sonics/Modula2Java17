package ch.pitchtech.modula.converter.model;

import java.util.ArrayList;
import java.util.List;

import ch.pitchtech.modula.converter.model.source.NodeAttachType;

/**
 * Group related nodes, such as all variable definition
 * <p>
 * This is mainly used so that items of different types are not siblings.
 * This allows us to use for exemple:
 * <ul>
 * <li>NodeList&lt;TypeDefinition&gt;
 * <ul>
 * <li>TypeDefinition 1
 * <li>TypeDefinition 2
 * </ul>
 * <li>NodeList&lt;VariableDefinition&gt;
 * <ul>
 * <li>VariableDefinition 1
 * <li>VariableDefinition 2
 * </ul>
 * </ul>
 * Instead of just
 * <ul>
 * <li>TypeDefinition 1
 * <li>TypeDefinition 2
 * <li>VariableDefinition 1
 * <li>VariableDefinition 2
 * </ul>
 */
public class NodeList<T extends INode> implements INode {
    
    private final INode parent;
    private final List<T> elements;
    private final NodeAttachType attachType;
    
    
    public NodeList(INode parent, List<T> elements, NodeAttachType attachType) {
        this.parent = parent;
        this.elements = elements;
        this.attachType = attachType;
    }

    @Override
    public INode getParentNode() {
        return parent;
    }
    
    @Override
    public void setParentNode(INode parent) {
        throw new UnsupportedOperationException();
    }

    public List<T> getElements() {
        return elements;
    }

    @Override
    public List<INode> getChildren() {
        List<INode> result = new ArrayList<>();
        result.addAll(elements);
        return result;
    }
    
    @Override
    public NodeAttachType getAttachType(INode child) {
        return this.attachType;
    }

    @Override
    public String toString() {
        return "NodeList [elements=" + elements + "]";
    }

    static void toTreeString(INode node, String indent, StringBuilder result) {
        result.append(indent);
        if (node instanceof NodeList)
            result.append("NodeList");
        else
            result.append(node.toString());
        result.append("\n");
        for (INode child : node.getChildren()) {
            toTreeString(child, indent + "  ", result);
        }
    }

}
