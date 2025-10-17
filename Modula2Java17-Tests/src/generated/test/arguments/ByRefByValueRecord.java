package generated.test.arguments;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class ByRefByValueRecord {

    // Imports
    private final InOut inOut = InOut.instance();


    // TYPE

    @SuppressWarnings("unused")
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


    // VAR

    private Point point = new Point();


    public Point getPoint() {
        return this.point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }


    // PROCEDURE

    private void ModifyByRef(/* VAR */ Point value) {
        // VAR
        Point p2 = new Point();

        p2.x = 42;
        p2.y = 42;
        value.copyFrom(p2);
    }

    private void ModifyByValue(Point _value) {
        Point value = _value.newCopy(); // By-value and written argument

        // VAR
        Point p2 = new Point();

        p2.x = 42;
        p2.y = 42;
        value.copyFrom(p2);
    }

    private void ModifyByRefByRef(/* VAR+WRT */ Point value) {
        ModifyByRef(value);
    }

    private void ModifyByRefByValue(/* var */ Point value) {
        ModifyByValue(value);
    }

    private void ModifyByValueByRef(/* WRT */ Point _value) {
        Point value = _value.newCopy(); // By-value and written argument

        ModifyByRef(value);
    }

    private void ModifyByValueByValue(Point value) {
        ModifyByValue(value);
    }


    // Life Cycle

    private void begin() {
        InOut.instance().begin();

        point.x = 10;
        point.y = 10;
        ModifyByValueByValue(point);
        ModifyByRefByValue(point);
        ModifyByValueByRef(point);
        inOut.WriteInt(point.x, 2);
        inOut.WriteLn();
        ModifyByRefByRef(point);
        inOut.WriteInt(point.x, 2);
        inOut.WriteLn();
    }

    private void close() {
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        ByRefByValueRecord instance = new ByRefByValueRecord();
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