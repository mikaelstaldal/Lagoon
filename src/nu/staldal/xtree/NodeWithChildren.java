/*
 * Copyright (c) 2001, Mikael Ståldal
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * Note: This is known as "the modified BSD license". It's an approved
 * Open Source and Free Software license, see
 * http://www.opensource.org/licenses/
 * and
 * http://www.gnu.org/philosophy/license-list.html
 */

package nu.staldal.xtree;

import java.util.Vector;

public abstract class NodeWithChildren extends Node
{
    private Vector children;

    public NodeWithChildren(int capacity)
    {
        if (capacity >= 0)
            children = new Vector(capacity);
        else
            children = new Vector();
    }

    public int numberOfChildren()
    {
        return children.size();
    }

    public Node getChild(int index)
        throws ArrayIndexOutOfBoundsException
    {
        return (Node)children.elementAt(index);
    }

    public void addChild(Node newChild)
    {
        newChild.setParent(this);
        children.addElement(newChild);
    }

    public Node replaceChild(Node newChild, int index)
        throws ArrayIndexOutOfBoundsException
    {
        Node oldChild = (Node)children.elementAt(index);
        oldChild.setParent(null);
        newChild.setParent(this);
        children.setElementAt(newChild, index);
        return oldChild;
    }

    /**
     * Inefficient
     */
    public Node removeChild(int index)
        throws ArrayIndexOutOfBoundsException
    {
        Node child = (Node)children.elementAt(index);
        children.removeElementAt(index);
        child.setParent(null);
        return child;
    }

    /**
     * Inefficient
     */
    public void insertChild(Node newChild, int index)
        throws ArrayIndexOutOfBoundsException
    {
        newChild.setParent(this);
        children.insertElementAt(newChild, index);
    }
}
