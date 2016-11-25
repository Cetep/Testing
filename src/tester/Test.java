package tester;

/**
 * Created by Petec on 22.11.2016.
 */
public class Test {
    private String className;
    private String testElement;
    private String testSpecification;
    private String testValue;

    public Test(String className, String testElement, String testSpecification, String testValue) {
        this.className = className;
        this.testElement = testElement;
        this.testSpecification = testSpecification;
        this.testValue = testValue;
        System.out.println("cn: " + className);
        System.out.println("te: " + testElement);
        System.out.println("ts: " + testSpecification);
        System.out.println("tv: " + testValue);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTestElement() {
        return testElement;
    }

    public void setTestElement(String testElement) {
        this.testElement = testElement;
    }

    public String getTestSpecification() {
        return testSpecification;
    }

    public void setTestSpecification(String testSpecification) {
        this.testSpecification = testSpecification;
    }

    public String getTestValue() {
        return testValue;
    }

    public void setTestValue(String testValue) {
        this.testValue = testValue;
    }
}
