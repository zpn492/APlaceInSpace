package BrandtArchive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Joa
 */
public class Archive {
    /**
     * Initialize a already existing file, 
     * enable read & write methods
     * 
     * @param path 
     */
    public Archive(String path) {    
        this.initialized = true;
        File f = new File(path);
        if(f.exists()) {
            try {
                this.file = new RandomAccessFile(f.getAbsolutePath(), "rw");
            }
            catch(FileNotFoundException e) {
                System.out.println("BrandtArchive.Archive: "+e.getMessage());
                this.initialized = false;
            }
        }
        else {
            this.initialized = false;
        }
    }
    
    public void close() {
        try {
            if(initialized)
                file.close();
        } catch(IOException e) {
            System.out.println("BrandtArchive.Archive: "+e.getMessage());
        }
    }
    
    /**
     * @return null upon IOException, EOF or !Initialized
     */
    public String readLine() {
        String str = null;
        if(this.initialized) {
            try {
                str = file.readLine();   
            }
            catch(IOException e) {
                System.out.println("BrandtArchive.Archive: "+e.getMessage());
            }
        }
        return str;
    }
    /**
     * length of the file
     * @return -1 upon IOException
     */
    public long size() {
        long length = -1;
        if(initialized) {
            try {
                length = file.length();
            }
            catch(IOException e) {
                System.out.println("BrandtArchive.Archive "+e.getMessage());
            }
        }
        return length;
    }
    /**
     * Read a part of a file,  
     * 
     * @param start seek the start position
     * @param length read upto the size of length
     * @return null upon IO-, NullPointerException or !initialized
     */
    public byte[] readPart(int start, int length) {
        if(this.initialized) {
            byte[] buf = new byte[length];
            try {
                file.seek(start);
                int read;
                while((read = file.read(buf)) != -1) {
                    if ((length -= read) > 0) {}
                    else  break;
                }
                file.seek(0);
            }
            catch(IOException e) {
                System.out.println("BrandtArchive.Archive: "+e.getMessage());
                buf = null;
            }
            catch(NullPointerException e) {
                System.out.println("BrandtArchive.Archive: "+e.getMessage());
                buf = null;
            }
            return buf;
        }
        return null;
    }
    /**
     * Try to write a byte[] into a file
     * 
     * @param buf
     * @return false upon IOException
     */
    public boolean write(byte[] buf) {
        boolean success = true;
        if(this.initialized) {
            try {
                file.write(buf, 0, buf.length);
            }
            catch(IOException e) {
                System.out.println("BrandtArchive.Archive: "+e.getMessage());
                success = false;
            }
        }
        else {
            success = false;
        }
        return success;
    }
    /**
     * Whether or not the tube is initialized
     * @return false upon constructor failure
     */
    public boolean isInitialized() {
        return this.initialized;
    }
       
    private RandomAccessFile file;
    private boolean initialized;
}