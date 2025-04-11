package ch.pitchtech.modula.converter.model;

import java.util.List;

import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.statement.Assignement;

/**
 * Node of the Abstract Syntax Tree.
 * <p>
 * Implemented by statements, expressions, definitions (vars, types, etc), etc...
 * <p>
 * The whole Modula-2 source code is represented by a tree of <tt>INode</tt>s,
 * except comments and white spaces that are excluded.
 */
public interface INode {
    
    public INode getParentNode();
    
    public void setParentNode(INode parent);
    
    public List<INode> getChildren();
    
    public NodeAttachType getAttachType(INode child);
    
    /**
     * Whether this node is an expression that is read, an expression that is written, or none of the above.
     * In an assignment for instance, the target item of the left side is written and rest of the left side
     * as well as the right side are read. Statements are neither read nor written.
     * <p>
     * Initialized by individual constructors of model items (such as {@link Assignement#Assignement}),
     * using <tt>SourceElement#attach</tt>.
     */
    public default NodeAttachType getAttachType() {
        return getParentNode().getAttachType(this);
    }
    
    public default String toTreeString() {
        StringBuilder result = new StringBuilder();
        NodeList.toTreeString(this, "", result);
        return result.toString();
    }
    
    /**
     * Move this node from its current parent to another parent
     * @param oldParent the current parent
     * @param newParent the new parent
     * @param before the child of new parent before which to insert this node. It must be a child of 'newParent',
     * or null to insert as the last child.
     */
    public default void move(INode oldParent, INode newParent, INode before) {
        INode parent = getParentNode();
        if (parent != oldParent)
            throw new CompilerException(this, "Wrong parent node: {0} != {1}", parent, oldParent);
        oldParent.getChildren().remove(this);
        if (before == null) {
            // Add at the end
            newParent.getChildren().add(this);
        } else {
            int index = newParent.getChildren().indexOf(before);
            if (index < 0)
                throw new CompilerException(this, "Node {0} is not a child of {1}", before, newParent);
            newParent.getChildren().add(index, this);
        }
        setParentNode(newParent);
    }
    
    public default void remove(INode parent) {
        INode parent0 = getParentNode();
        if (parent0 != parent)
            throw new CompilerException(this, "Wrong parent node: {0} != {1}", parent, parent0);
        parent.getChildren().remove(this);
        setParentNode(null);
    }

}
