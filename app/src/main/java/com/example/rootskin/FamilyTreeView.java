//package com.example.rootskin;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.util.AttributeSet;
//import android.view.View;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FamilyTreeView extends View {
//
//    private Paint paint;
//    private List<FamilyTreeNode> familyTreeNodes;  // Store FamilyTreeNode objects
//    private int nodeWidth = 300;  // Width of each node
//    private int nodeHeight = 100;  // Height of each node
//    private int horizontalSpacing = 200;  // Horizontal spacing between siblings
//    private int verticalSpacing = 150;  // Vertical spacing between generations
//
//    public FamilyTreeView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setTextSize(30);
//        familyTreeNodes = new ArrayList<>();
//    }
//
//    public void setFamilyMembers(List<FamilyTreeNode> familyTreeNodes) {
//        this.familyTreeNodes = familyTreeNodes;
//        invalidate();  // Redraw the view when data is updated
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        // Clear the canvas with a white background
//        canvas.drawColor(0xFFFFFFFF);
//
//        // Start drawing the family tree from the root node
//        if (familyTreeNodes != null && !familyTreeNodes.isEmpty()) {
//            drawFamilyTree(canvas, familyTreeNodes, getWidth() / 2, 100);
//        }
//    }
//
//    // Recursive method to draw the family tree
//    private void drawFamilyTree(Canvas canvas, List<FamilyTreeNode> nodes, int x, int y) {
//        int childX = x - (nodes.size() * horizontalSpacing) / 2;  // Calculate the starting X for siblings
//
//        // Loop through all nodes at this level
//        for (FamilyTreeNode node : nodes) {
//            // Set paint color for the rectangle (light gray for visibility)
//            paint.setColor(0xFFDDDDDD);  // Light gray background for the node
//            Rect rect = new Rect(childX, y, childX + nodeWidth, y + nodeHeight);
//            canvas.drawRect(rect, paint);  // Draw the node box
//
//            // Set paint color for the text (black for visibility)
//            paint.setColor(0xFF000000);  // Black text
//            canvas.drawText(node.getFamilyMember().getFirstName() + " " + node.getFamilyMember().getLastName(),
//                    childX + 20, y + nodeHeight / 2, paint);  // Draw the name inside the node
//
//            childX += horizontalSpacing;  // Move to the next sibling position
//
//            // Draw lines to children (if any)
//            if (!node.getChildren().isEmpty()) {
//                int childY = y + verticalSpacing;  // The Y position for the next generation
//                for (FamilyTreeNode childNode : node.getChildren()) {
//                    canvas.drawLine(childX - horizontalSpacing / 2, y + nodeHeight, childX - horizontalSpacing / 2, childY, paint);
//                }
//                drawFamilyTree(canvas, node.getChildren(), childX - horizontalSpacing / 2, childY);  // Recursively draw children
//            }
//        }
//    }
//
//    // Static inner class FamilyTreeNode
//    public static class FamilyTreeNode {
//        private FamilyMember familyMember;
//        private List<FamilyTreeNode> children;
//
//        // Constructor for FamilyTreeNode
//        public FamilyTreeNode(FamilyMember familyMember) {
//            this.familyMember = familyMember;
//            this.children = new ArrayList<>();
//        }
//
//        // Add child node
//        public void addChild(FamilyTreeNode child) {
//            children.add(child);
//        }
//
//        // Getter methods
//        public FamilyMember getFamilyMember() {
//            return familyMember;
//        }
//
//        public List<FamilyTreeNode> getChildren() {
//            return children;
//        }
//    }
//}
