package generated.test.memory;

import ch.pitchtech.modula.library.iso.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


@SuppressWarnings("unused")
public class NewDisposeTest {

    // Imports
    private final InOut inOut = InOut.instance();


    // TYPE

    private static class Point { // RECORD

        private int x;
        private int y;


        public int getX() {
            return this.x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return this.y;
        }

        public void setY(int y) {
            this.y = y;
        }


        public void copyFrom(Point other) {
            this.x = other.x;
            this.y = other.y;
        }

        public Point newCopy() {
            Point copy = new Point();
            copy.copyFrom(this);
            return copy;
        }

    }

    private static class Rectangle { // RECORD

        private Point topLeft = new Point();
        private Point bottomRightPtr /* POINTER */;


        public Point getTopLeft() {
            return this.topLeft;
        }

        public void setTopLeft(Point topLeft) {
            this.topLeft = topLeft;
        }

        public Point getBottomRightPtr() {
            return this.bottomRightPtr;
        }

        public void setBottomRightPtr(Point bottomRightPtr) {
            this.bottomRightPtr = bottomRightPtr;
        }


        public void copyFrom(Rectangle other) {
            this.topLeft.copyFrom(other.topLeft);
            this.bottomRightPtr = other.bottomRightPtr;
        }

        public Rectangle newCopy() {
            Rectangle copy = new Rectangle();
            copy.copyFrom(this);
            return copy;
        }

    }


    // VAR

    private Point point /* POINTER */;
    private Rectangle rectangle /* POINTER */;


    public Point getPoint() {
        return this.point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }


    // Life Cycle

    private void begin() {
        Storage.instance().begin();
        InOut.instance().begin();

        point = new Point();
        point.x = 10;
        point.y = 20;
        inOut.Write('(');
        inOut.WriteInt(point.x, 2);
        inOut.Write(',');
        inOut.WriteInt(point.y, 2);
        inOut.Write(')');
        inOut.WriteLn();
        point = null;
        rectangle = new Rectangle();
        rectangle.topLeft.x = 42;
        rectangle.topLeft.y = 84;
        inOut.Write('(');
        inOut.WriteInt(rectangle.topLeft.x, 2);
        inOut.Write(',');
        inOut.WriteInt(rectangle.topLeft.y, 2);
        inOut.Write(')');
        inOut.WriteLn();
        if (rectangle.bottomRightPtr == null) {
            inOut.WriteString("NIL");
            inOut.WriteLn();
        }
        rectangle.bottomRightPtr = new Point();
        rectangle.bottomRightPtr = null;
        rectangle = null;
    }

    private void close() {
        InOut.instance().close();
        Storage.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        NewDisposeTest instance = new NewDisposeTest();
        try {
            instance.begin();
        } catch (HaltException ex) {
            // Normal termination
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            instance.close();
        }
    }

}