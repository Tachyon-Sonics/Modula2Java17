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
    private EnumSet<MyEnum> set2 = EnumSet.noneOf(MyEnum.class);
    private EnumSet<MyEnum> set3 = EnumSet.noneOf(MyEnum.class);
    private MyEnum index;
    private boolean test;


    public EnumSet<MyEnum> getSet() {
        return this.set;
    }

    public void setSet(EnumSet<MyEnum> set) {
        this.set = set;
    }

    public EnumSet<MyEnum> getSet2() {
        return this.set2;
    }

    public void setSet2(EnumSet<MyEnum> set2) {
        this.set2 = set2;
    }

    public EnumSet<MyEnum> getSet3() {
        return this.set3;
    }

    public void setSet3(EnumSet<MyEnum> set3) {
        this.set3 = set3;
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
        set = Runtime.plusSet(set2, set3);
        set = Runtime.minusSet(set2, set3);
        set = Runtime.mulSet(set2, set3);
        set = Runtime.divSet(set2, set3);
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