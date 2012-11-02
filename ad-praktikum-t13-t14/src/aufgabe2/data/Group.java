package aufgabe2.data;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 02.11.12
 * Time: 17:49
 */
public class Group {

    private int groupID;
    private List<String> pathList;
    private String currentFile;

    private Group(int groupID, String file1Path, String file2Path) {
        this.groupID = groupID;
        final String file1 = file1Path;
        final String file2 = file2Path;
        pathList.addAll(Arrays.asList(file1,file2));
        currentFile = pathList.get(0);

    }

    public static Group createGroup(int groupID, String file1Path, String file2Path) {
        return new Group(groupID, file1Path, file2Path);
    }
    public static Group createGroup(String file1Path, String file2Path) {
        return new Group(0, file1Path, file2Path);
    }

    //Funktion

    public void switchFile(){
        switchFile(currentFile, pathList);
    }

    void switchFile(String currentFile, List<String> pathList){
        final int currentGroupIndex = pathList.indexOf(currentFile);

        if(currentGroupIndex+1 > pathList.size()-1){
            currentFile = pathList.get(0);
        }else{
            currentFile = pathList.get(currentGroupIndex+1);
        }
    }

    //Getter
    public int getGroupID() {
        return groupID;
    }

    public String getCurrentFile() {
        return currentFile;
    }
}
