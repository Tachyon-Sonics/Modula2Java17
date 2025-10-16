package generated.test.set;

import java.util.EnumSet;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class EnumSetAccess {

    // TYPE

    private static enum MyEnum {
        One,
        Two,
        Three,
        Sun;
    }


    // VAR

    private EnumSet<MyEnum> set = EnumSet.noneOf(MyEnum.class);
    private MyEnum index;
    private boolean test;


    public EnumSet<MyEnum> getSet() {
        return this.set;
    }

    public void setSet(EnumSet<MyEnum> set) {
        this.set = set;
    }

    public MyEnum getIndex() {
        return this.index;
    }

    public void setIndex(MyEnum index) {
        this.index = index;
    }

    public boolean isTest() {
        return this.test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }


    // Life Cycle

    private void begin() {
        index = MyEnum.One;
        set.add(index);
        index = MyEnum.Two;
        set.remove(index);
        index = MyEnum.Sun;
        test = (set.contains(index));
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        EnumSetAccess instance = new EnumSetAccess();
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