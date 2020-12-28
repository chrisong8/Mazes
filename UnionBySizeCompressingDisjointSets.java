package disjointsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
// import java.util.Set;
// import java.util.Stack;

/**
 * A quick-union-by-size data structure with path compression.
 * @see DisjointSets for more documentation.
 */
public class UnionBySizeCompressingDisjointSets<T> implements DisjointSets<T> {
    // Do NOT rename or delete this field. We will be inspecting it directly in our private tests.
    List<Integer> pointers;

    /*
    However, feel free to add more fields and private helper methods. You will probably need to
    add one or two more fields in order to successfully implement this class.
    */
    private HashMap<T, Integer> map;
    // stores indices of items
    private int size;

    public UnionBySizeCompressingDisjointSets() {
        this.pointers = new ArrayList<>();
        this.map = new HashMap<>();
        this.size = 0;
    }

    @Override
    public void makeSet(T item) {
        // -1 to back of "array"
        this.pointers.add(-1);
        this.map.put(item, size);
        this.size++;
    }

    @Override
    // finds index of root
    public int findSet(T item) {
        Integer index = this.map.get(item);
        if (Objects.equals(index, null)) {
            throw new IllegalArgumentException();
        }
        Stack<Integer> stack = new Stack<>();
        stack.add(index);
        while (pointers.get(index) >= 0) {
            index = pointers.get(index);
            stack.add(index);
        }
        Integer rootIndex = stack.pop();
        while (!stack.isEmpty()) {
            Integer top = stack.pop();
            pointers.set(top, rootIndex);
        }
        return index;
    }

    @Override
    public boolean union(T item1, T item2) {
        int rootA = findSet(item1);
        int rootB = findSet(item2);
        if (rootA == rootB) {
            return false;
        }
        // if A has bigger weight, put B under
        if (-1 * pointers.get(rootA) >= -1 * pointers.get(rootB)) {
            pointers.set(rootA, pointers.get(rootA) + pointers.get(rootB));
            pointers.set(rootB, rootA);
        } else {
            // if B has more weight, put A under
            pointers.set(rootB, pointers.get(rootB) + pointers.get(rootA));
            pointers.set(rootA, rootB);
        }
        this.size--;
        return true;
    }

    // finds key in map for a root index
    // public T findKey(int root) {
    //     for (T key : map.keySet()) {
    //         if (map.get(key).equals(root)) {
    //             return key;
    //         }
    //     }
    //     return null;
    // }
}
