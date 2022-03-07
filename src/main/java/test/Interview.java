package test;

import java.util.LinkedList;
import java.util.List;

public class Interview {
    private static Interview getInterviewInstance = null;
    private static int sizeOfArray = 3;
    private static Interview[] interviewArray = new Interview[sizeOfArray];
    private static int counter = 0;
    public static void main(String[] args) {
        Interview currentInstance = getInstance();
    }

    public static Interview getInstance(){
        synchronized (Interview.class){
            if(interviewArray[counter] == null || (counter%3 == 0)){
                getInterviewInstance = new Interview();
                counter++;
                return getInterviewInstance;
            }
            if(getInterviewInstance != null && counter%2 == 0){
                return null;
            }
            return getInterviewInstance;
        }
    }
}
