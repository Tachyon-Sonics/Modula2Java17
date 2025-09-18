package generated.test.arguments;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class ByRefByValue {

    // Imports
    private final InOut inOut = InOut.instance();


    // TYPE

    @SuppressWarnings("unused")
    private static class Point { // RECORD

        private short x;
        private short y;


        public short getX() {
            return this.x;
        }

        public void setX(short x) {
            this.x = x;
        }

        public short getY() {
            return this.y;
        }

        public void setY(short y) {
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

    private short value;
    private Point point = new Point();


    public short getValue() {
        return this.value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    public Point getPoint() {
        return this.point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }


    // PROCEDURE

    private void ModifyByRefInt(/* VAR */ Runtime.IRef<Short> value) {
        value.set((short) 42);
    }

    private void ModifyByValueInt(short value) {
        value = 42;
    }

    private void ModifyByRefByRefInt(/* VAR+WRT */ Runtime.IRef<Short> value) {
        ModifyByRefInt(value);
    }

    private void ModifyByRefByValueInt(/* var */ Runtime.IRef<Short> value) {
        ModifyByValueInt(value.get());
    }

    private void ModifyByValueByRefInt(/* WRT */ short _value) {
        Runtime.Ref<Short> value = new Runtime.Ref<>(_value);

        ModifyByRefInt(value);
    }

    private void ModifyByValueByValueInt(short value) {
        ModifyByValueInt(value);
    }

    private void ModifyByRef(/* var */ Point value) {
        value.x = 42;
    }

    private void ModifyByValue(Point value) {
        value.x = 42;
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

        value = 10;
        ModifyByValueByValueInt(value);
        ModifyByRefByValueInt(new Runtime.FieldRef<>(this::getValue, this::setValue));
        ModifyByValueByRefInt(value);
        inOut.WriteInt(value, 2);
        inOut.WriteLn();
        ModifyByRefByRefInt(new Runtime.FieldRef<>(this::getValue, this::setValue));
        inOut.WriteInt(value, 2);
        inOut.WriteLn();
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
        ByRefByValue instance = new ByRefByValue();
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
