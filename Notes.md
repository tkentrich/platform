#Notes

A *much* simplet way to determine collisions.
Since all objects are rectangles, simply check to see if one boundary is "further" than the opposite boundary on the second component.

private boolean collision (Component A, Component B) {
    if (A.position().x() > B.position().plus(B.size()).x() // A to the right of B
     || A.position().y() > B.position().plus(B.size()).y() // A below B
     || A.position().plus(A.size()).x() < B.position().x() // A to the left of B
     || A.position().plus(A.size()).y() < B.position().y() // A above B
       ) {
        return false;
    }
}
