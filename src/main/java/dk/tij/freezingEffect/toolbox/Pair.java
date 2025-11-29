package dk.tij.freezingEffect.toolbox;

public class Pair<K, V> {
    public static <K, V> Pair<K, V> of(K k, V v) {
        return new Pair<>(k, v);
    }

    private K k;
    private V v;

    public Pair() {}

    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public void set(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public void setK(K k) {
        this.k = k;
    }

    public void setV(V v) {
        this.v = v;
    }

    public K getK() {
        return k;
    }

    public V getV() {
        return v;
    }

    @Override
    public String toString() {
        return String.format("Pair[%s, %s]", k, v);
    }
}
