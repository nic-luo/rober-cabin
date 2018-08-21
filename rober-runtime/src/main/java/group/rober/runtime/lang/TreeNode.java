package group.rober.runtime.lang;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeNode<T extends TreeNode> {
    protected T parent;
    protected List<T> children;

    @JsonIgnore
    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }

    public void addChild(T value) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(value);
        value.setParent(this);
    }

    public void addChilds(List<T> value) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        value.forEach(this::addChild);
    }
}
