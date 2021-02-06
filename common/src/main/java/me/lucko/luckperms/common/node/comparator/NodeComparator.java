/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.common.node.comparator;

import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {
    private static final Comparator<? super Node> INSTANCE = new NodeComparator();
    private static final Comparator<? super Node> REVERSE = INSTANCE.reversed();

    public static Comparator<? super Node> normal() {
        return INSTANCE;
    }

    public static Comparator<? super Node> reverse() {
        return REVERSE;
    }

    @SuppressWarnings({"ConstantConditions", "OptionalGetWithoutIsPresent"})
    @Override
    public int compare(Node o1, Node o2) {
        if (o1.equals(o2)) {
            return 0;
        }

        // compare whether nodes are temporary
        int result = Boolean.compare(o1.hasExpiry(), o2.hasExpiry());
        if (result != 0) {
            return result;
        }

        // compare whether nodes are wildcard nodes
        result = Boolean.compare(
                o1 instanceof PermissionNode && ((PermissionNode) o1).isWildcard(),
                o2 instanceof PermissionNode && ((PermissionNode) o2).isWildcard()
        );
        if (result != 0) {
            return result;
        }

        // compare expiry times if both nodes are temporary
        // due to the comparison earlier, either both nodes are temporary or neither are.
        if (o1.hasExpiry()) {
            result = o1.getExpiry().compareTo(o2.getExpiry());
            if (result != 0) {
                return result;
            }
        }

        // compare wildcard level if both nodes are wildcards
        // due to the comparison earlier, either both nodes are wildcards or neither are
        if (o1 instanceof PermissionNode && ((PermissionNode) o1).isWildcard()) { // implies o2.isWildcard too
            int o1Level = ((PermissionNode) o1).getWildcardLevel().getAsInt();
            int o2Level = ((PermissionNode) o2).getWildcardLevel().getAsInt();

            result = Integer.compare(o1Level, o2Level);
            if (result != 0) {
                return result;
            }
        }

        // compare node keys lexicographically
        // note that the order is reversed - A comes before Z
        result = -o1.getKey().compareTo(o2.getKey());
        if (result != 0) {
            return result;
        }

        // compare the boolean node values
        // note that the order is reversed, false comes before true
        result = -Boolean.compare(o1.getValue(), o2.getValue());
        if (result != 0) {
            return result;
        }

        throw new AssertionError("nodes are equal? " + o1 + " - " + o2);
    }
}
