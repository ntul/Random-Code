import java.util.*;
import java.io.*;

public class BTree {

    static int splits = 0;
    static int parentSplits = 0;
    static int fusions = 0;
    static int parentFusions = 0;
    static int depth = 0;
    static BTree firstValueNode;
    BTree parent; // Pointer to a parent node
    BTree sibling;
    Boolean isLeaf = false; // Is a node a leaf Flag
    ArrayList<String> keys; // Array of keys
    ArrayList<BTree> children; // Array of pointers to children
    ArrayList<String> values; // Array of values for leaf nodes

    BTree() {
	this.keys = new ArrayList<String>();
	this.children = new ArrayList<BTree>();
	this.values = new ArrayList<String>();
	this.isLeaf = true;
    }

    BTree(ArrayList<String> keys, ArrayList<BTree> children) {

	this.keys = keys;
	this.children = children;
    }

    BTree(ArrayList<String> values) {

	this.values = values;
	this.isLeaf = true;
    }

    BTree search(BTree tree, String value) { // Search method that returns a
					     // node

	if (tree.isLeaf == true)
	    return tree;

	else {
	    if (value.compareTo(tree.keys.get(0).substring(0, 7)) < 0)
		return search(tree.children.get(0), value);
	    else if (tree.keys.size() < 2)
		return search(tree.children.get(1), value);
	    else if (value.compareTo(tree.keys.get(1).substring(0, 7)) < 0)
		return search(tree.children.get(1), value);
	    else if (tree.keys.size() < 3)
		return search(tree.children.get(2), value);
	    else if (value.compareTo(tree.keys.get(2).substring(0, 7)) < 0)
		return search(tree.children.get(2), value);
	    else if (tree.keys.size() < 4)
		return search(tree.children.get(3), value);
	    else if (value.compareTo(tree.keys.get(3).substring(0, 7)) < 0)
		return search(tree.children.get(3), value);
	    else
		return search(tree.children.get(4), value);
	}
    }

    String searchVal(BTree tree, String value) { // Search method that returns a
						 // string

	ArrayList<String> found = new ArrayList<String>();
	String foundStr = null;
	found.addAll(tree.search(tree, value).values);
	for (String p : found)
	    if (p.substring(0, 7).equals(value))
		foundStr = p;
	if (foundStr == null)
	    return "Not Found";
	else
	    return foundStr;
    }

    void insert(BTree tree, String value) { // Insert method

	BTree tr = search(tree, value);
	tr.values.add(value);
	tree.sort(tr.values);

	if (tr.values.size() > 16)
	    split(tr);

    }

    void delete(BTree tree, String value) { // Delete method

	BTree tr = search(tree, value);
	for (int i = 0; i < tr.values.size(); i++)
	    if (tr.values.get(i).substring(0, 7).equals(value))
		tr.values.remove(i);

	if (tr.parent != null && tr.values.size() < 8)
	    merge(tr);

    }

    void modify(BTree tree, String value, String newValue) { // Modify method

	BTree tr = search(tree, value);
	for (int i = 0; i < tr.values.size(); i++)
	    if (tr.values.get(i).substring(0, 7).equals(value))
		tr.values.set(i, value + "        " + newValue);

    }

    void merge(BTree tree) { // Merge method

	if (tree.isLeaf) { // If node is a leaf
	    fusions++;
	    if (tree.parent.parent != null) {
		BTree sibling = new BTree();
		Boolean nextSib = false;
		int i = 0;
		while (true) {
		    if (tree.parent.children.get(i) == tree) {
			if (i != tree.parent.children.size() - 1) {
			    sibling = tree.parent.children.get(i + 1);
			    nextSib = true;
			    break;
			} else {
			    sibling = tree.parent.children.get(i - 1);
			    break;
			}
		    } else
			i++;
		}
		if (sibling.values.size() > 8) { // Borrowing from sibling
		    if (nextSib == true) {
			tree.values.add(sibling.values.get(0));
			sibling.values.remove(0);
			tree.parent.keys.set(i, sibling.values.get(0));
		    } else {
			sibling.values.add(tree.values.get(0));
			tree.values.remove(0);
			tree.parent.keys.set(i - 1, sibling.values.get(0));
		    }
		} else { // Merging siblings
		    if (nextSib == true) {
			tree.values.addAll(sibling.values);
			tree.parent.keys.remove(i);
			tree.parent.children.remove(i + 1);
			tree.sibling = sibling.sibling;
		    } else {
			sibling.values.addAll(tree.values);
			tree.parent.keys.remove(i - 1);
			tree.parent.children.remove(i);
			sibling.sibling = tree.sibling;
		    }
		}
	    } else { // If node is leaf and parent is root
		BTree sibling = new BTree();
		Boolean nextSib = false;
		int i = 0;
		while (true) {
		    if (tree.parent.children.get(i) == tree) {
			if (i != tree.parent.children.size() - 1) {
			    sibling = tree.parent.children.get(i + 1);
			    nextSib = true;
			    break;
			} else {
			    sibling = tree.parent.children.get(i - 1);
			    break;
			}
		    } else
			i++;
		}

		if (nextSib == true) {
		    tree.parent.values.addAll(sibling.values);
		    tree.parent.values.addAll(tree.values);
		    tree.sort(tree.parent.values);
		    tree.parent.children.clear();
		    tree.parent.isLeaf = true;
		    tree.parent.keys.clear();
		    tree.sibling = sibling.sibling;
		} else {
		    tree.parent.values.addAll(sibling.values);
		    tree.parent.values.addAll(tree.values);
		    tree.sort(tree.parent.values);
		    tree.parent.children.clear();
		    tree.parent.isLeaf = true;
		    tree.parent.keys.clear();
		    sibling.sibling = tree.sibling;
		}
	    }
	} else { // If node is not a leaf
	    parentFusions++;
	    BTree sibling = new BTree();
	    Boolean nextSib = false;
	    int i = 0;
	    while (true) {
		if (tree.parent.children.get(i) == tree) {
		    if (i != tree.parent.children.size() - 1) {
			sibling = tree.parent.children.get(i + 1);
			nextSib = true;
			break;
		    } else {
			sibling = tree.parent.children.get(i - 1);
			break;
		    }
		} else
		    i++;
	    }
	    if (sibling.keys.size() > 2) { // Borrowing from sibling
		if (nextSib == true) {
		    tree.keys.add(sibling.keys.get(0));
		    sibling.keys.remove(0);
		    tree.parent.keys.set(i, sibling.keys.get(0));
		} else {
		    sibling.keys.add(tree.keys.get(0));
		    tree.keys.remove(0);
		    tree.parent.keys.set(i - 1, sibling.keys.get(0));
		}
	    } else { // Merging with a sibling
		if (nextSib == true) {
		    tree.keys.add(tree.parent.keys.get(i));
		    tree.parent.keys.remove(i);
		    tree.parent.children.remove(i + 1);
		    tree.keys.addAll(sibling.keys);
		    tree.children.add(sibling.children.get(0));
		    tree.children.add(sibling.children.get(1));
		    tree.children.add(sibling.children.get(2));
		    tree.children.get(2).parent = tree;
		    tree.children.get(3).parent = tree;
		    tree.children.get(4).parent = tree;
		} else {
		    sibling.keys.add(tree.parent.keys.get(i - 1));
		    sibling.parent.keys.remove(i - 1);
		    sibling.parent.children.remove(i);
		    sibling.keys.addAll(tree.keys);
		    sibling.children.add(tree.children.get(0));
		    sibling.children.add(tree.children.get(1));
		    sibling.children.get(2).parent = sibling;
		    sibling.children.get(3).parent = sibling;
		}
	    }
	}

	if (tree.parent.parent != null && tree.parent.keys.size() < 2)
	    merge(tree.parent);
    }

    void split(BTree tree) { // Split method

	if (tree.parent == null && tree.isLeaf) {
	    depth++;
	    splits++;
	    BTree newChild1 = new BTree();
	    BTree newChild2 = new BTree(new ArrayList<String>(tree.values
		    .subList(tree.values.size() / 2, tree.values.size())));
	    firstValueNode = newChild1;
	    newChild1.sibling = newChild2;
	    newChild1.isLeaf = true;
	    newChild1.values.addAll(tree.values);
	    newChild1.values.removeAll(tree.values
		    .subList(tree.values.size() / 2, tree.values.size()));
	    tree.children.add(newChild1);
	    tree.children.add(newChild2);
	    newChild1.parent = tree;
	    newChild2.parent = tree;
	    tree.keys.add(tree.values.get(tree.values.size() / 2));
	    tree.values.clear();
	    tree.isLeaf = false;
	} else if (tree.parent == null) {
	    depth++;
	    parentSplits++;
	    BTree newChild1 = new BTree();
	    BTree newChild2 = new BTree();
	    newChild1.isLeaf = false;
	    newChild2.isLeaf = false;
	    newChild1.keys.add(tree.keys.get(0));
	    newChild1.keys.add(tree.keys.get(1));
	    newChild1.children.add(tree.children.get(0));
	    newChild1.children.add(tree.children.get(1));
	    newChild1.children.add(tree.children.get(2));
	    newChild1.children.get(0).parent = newChild1;
	    newChild1.children.get(1).parent = newChild1;
	    newChild1.children.get(2).parent = newChild1;
	    newChild2.keys.add(tree.keys.get(3));
	    newChild2.keys.add(tree.keys.get(4));
	    newChild2.children.add(tree.children.get(3));
	    newChild2.children.add(tree.children.get(4));
	    newChild2.children.add(tree.children.get(5));
	    newChild2.children.get(0).parent = newChild2;
	    newChild2.children.get(1).parent = newChild2;
	    newChild2.children.get(2).parent = newChild2;
	    newChild1.parent = tree;
	    newChild2.parent = tree;
	    tree.keys.retainAll(Arrays.asList(tree.keys.get(2)));
	    tree.children.clear();
	    tree.children.add(newChild1);
	    tree.children.add(newChild2);
	} else if (tree.isLeaf) {
	    splits++;
	    tree.parent.keys.add(tree.values.get(tree.values.size() / 2));
	    sort(tree.parent.keys);
	    BTree newSibling = new BTree(new ArrayList<String>(tree.values
		    .subList(tree.values.size() / 2, tree.values.size())));
	    newSibling.sibling = tree.sibling;
	    tree.sibling = newSibling;
	    tree.values.removeAll(tree.values.subList(tree.values.size() / 2,
		    tree.values.size()));
	    tree.parent.children.add(newSibling);
	    newSibling.parent = tree.parent;

	} else {
	    parentSplits++;
	    tree.parent.keys.add(tree.keys.get(tree.keys.size() / 2));
	    sort(tree.parent.keys);
	    BTree newSibling = new BTree();
	    tree.keys.remove(tree.keys.size() / 2);
	    newSibling.keys.addAll(
		    tree.keys.subList(tree.keys.size() / 2, tree.keys.size()));
	    newSibling.isLeaf = false;
	    tree.keys.removeAll(
		    tree.keys.subList(tree.keys.size() / 2, tree.keys.size()));
	    tree.parent.children.add(newSibling);
	    newSibling.parent = tree.parent;
	    newSibling.children.addAll(tree.children
		    .subList(tree.children.size() / 2, tree.children.size()));
	    tree.children.removeAll(tree.children
		    .subList(tree.children.size() / 2, tree.children.size()));
	    newSibling.children.get(0).parent = newSibling;
	    newSibling.children.get(1).parent = newSibling;
	    newSibling.children.get(2).parent = newSibling;

	}

	if (tree.parent != null && tree.parent.keys.size() > 4)
	    split(tree.parent);
    }

    void fileRead(String fileName, ArrayList<String> parts)
	    throws FileNotFoundException { // Reading from a file method

	BufferedReader bin = new BufferedReader(new FileReader(fileName));
	try {

	    String line = bin.readLine();

	    while (line != null) {
		parts.add(line);
		line = bin.readLine();
	    }
	    bin.close();
	} catch (Exception e) {
	    System.out.println(e);

	}

    }

    void filewrite(String fileName) throws IOException { // Reading from a file
							 // method

	File file = new File(fileName);
	file.delete();
	File file1 = new File(fileName);
	BufferedWriter bin = new BufferedWriter(new FileWriter(fileName));
	try {

	    BTree trav = firstValueNode;

	    while (true) {
		for (String p : trav.values) {
		    bin.write(p);
		    bin.newLine();
		}
		if (trav.sibling == null)
		    break;
		trav = trav.sibling;
	    }

	    bin.close();
	} catch (Exception e) {
	    System.out.println(e);

	}

    }

    void sort(ArrayList<String> arr) {
	String dummy = "";
	for (int j = 0; j < arr.size(); j++) {
	    for (int i = 0; i < arr.size() - 1; i++) {
		if (arr.get(i + 1).substring(0, 7)
			.compareTo(arr.get(i).substring(0, 7)) < 0) {
		    dummy = arr.get(i);
		    arr.set(i, arr.get(i + 1));
		    arr.set(i + 1, dummy);
		}
	    }
	}
    }

    public static void main(String[] args) throws IOException { // Driver method

	ArrayList<String> parts = new ArrayList<String>();
	
	BTree tree = new BTree();
	tree.fileRead("part_file.txt", parts);
	for (int i = 0; i < parts.size(); i++) {
	    tree.insert(tree, parts.get(i));
	}

	int opt = 0;
	String pt = "";
	

	Scanner in = new Scanner(System.in);
	Start: while (true) {
	    System.out.println("Choose an option:");
	    System.out.println("1:Find part");
	    System.out.println("2:Add part");
	    System.out.println("3:Exit");
	    System.out.println("4:Save and Exit");
	    opt = in.nextInt();
	    in.nextLine();

	    switch (opt) {

	    case 1: {
		System.out.println();
		System.out.println("Enter part ID");
		pt = in.nextLine();
		System.out.println();

		if (tree.searchVal(tree, pt).equals("Not Found")) {
		    System.out.println("Part not found");
		    System.out.println("Press ENTER to continue");
		    in.nextLine();
		    continue Start;
		} else {
		    System.out.println("The part is:");
		    System.out.println(tree.searchVal(tree, pt));
		    System.out.println();
		    System.out.println("Choose an option:");
		    System.out.println("1:Delete part");
		    System.out.println("2:Modify part");
		    System.out.println("3:Print next 10 parts");
		    System.out.println("4:exit");
		    System.out.println();
		}
		opt = in.nextInt();
		in.nextLine();

		switch (opt) {
		case 1: {
		    tree.delete(tree, pt);
		    System.out.println("Part deleted");
		    System.out.println("Press enter to continue");
		    in.nextLine();
		    continue Start;
		}
		case 2: {
		    System.out.println("Enter new description");
		    String newVal = in.nextLine();
		    tree.modify(tree, pt, newVal);
		    System.out.println("New part is:");
		    System.out.println(tree.searchVal(tree, pt));
		    System.out.println("Press enter to continue");
		    in.nextLine();
		    continue Start;
		}
		case 3: {
		    System.out.println("Next 10 parts are:");
		    BTree nxt = tree.search(tree, pt);
		    int j = 0;
		    while (nxt.values.get(j)
			    .equals(tree.searchVal(tree, pt)) == false)
			j++;
		    j++;
		    for (int i = 0; i < 11; i++) {
			if (nxt.values.size() > j) {
			    System.out.println(nxt.values.get(j));
			    j++;
			} else {
			    nxt = nxt.sibling;
			    j = 0;
			}
		    }
		    System.out.println("Press ENTER to continue");
		    in.nextLine();
		    continue Start;

		}
		case 4: {
		    continue Start;
		}
		}
		break;
	    }
	    case 2: {
		    System.out.println("Enter new ID");
		    String newID = in.nextLine();
		    System.out.println("Enter new Description");
		    String newDesc = in.nextLine();
		    String newPart = newID+"        "+newDesc;
		    tree.insert(tree, newPart);
		    System.out.println("New part added");
		    System.out.println("Press enter to continue");
		    in.nextLine();
		    continue Start;
	    }
	    case 3: {
		break Start;
	    }
	    case 4: {
		tree.filewrite("part_file.txt");
		break Start;
	    }

	    }

	}
	System.out.println("Depth-" + depth);
	System.out.println("Splits-" + splits);
	System.out.println("Parent Splits-" + parentSplits);
	System.out.println("Fusions-" + fusions);
	System.out.println("Parent Fusions-" + parentFusions);
	System.out.println("Done");
    }

}
