package group.rober.runtime.lang;

import java.io.Serializable;

public class PairBond<L, R> implements Comparable<PairBond<L, R>>, Serializable,Cloneable {
    protected L left;
    protected R right;

    public PairBond() {
    }

    public PairBond(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> PairBond<L, R> of(final L left, final R right) {
        return new PairBond<L, R>(left, right);
    }

    public L getLeft() {
        return left;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public R getRight() {
        return right;
    }

    public void setRight(R right) {
        this.right = right;
    }

    public int compareTo(PairBond<L, R> o) {
        return 0;
    }
}
