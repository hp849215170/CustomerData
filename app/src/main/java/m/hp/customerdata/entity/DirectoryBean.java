package m.hp.customerdata.entity;

public class DirectoryBean {
    private String currentDir;
    private String dirName;
    private String lastModifyTime;
    private int folderImgId;

    public String getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public int getFolderImgId() {
        return folderImgId;
    }

    public void setFolderImgId(int folderImg) {
        this.folderImgId = folderImgId;
    }
}
