package com.krish.etl.scheduler.jobs;

import com.krish.etl.scheduler.dao.SourceFileDao;
import com.krish.etl.scheduler.dao.SourceFileMonitorDao;
import com.krish.etl.scheduler.pojo.Health;
import com.krish.etl.scheduler.pojo.SourceFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

public class SourceFilesMonitor extends TimerTask {
    private final Logger logger = LoggerFactory.getLogger(SourceFilesMonitor.class);
    private final SourceFileDao sourceFilesDao;
    private final SourceFileMonitorDao sourceFileMonitorDao;

    private final Health health = new Health();

    public SourceFilesMonitor(SourceFileDao sourceFilesDao, SourceFileMonitorDao sourceFileMonitorDao) {
        this.sourceFilesDao = sourceFilesDao;
        this.sourceFileMonitorDao = sourceFileMonitorDao;
        this.health.setHealthCheck("SourceFilesMonitor");
    }

    public List<SourceFile> getFileList() throws SQLException {
        return sourceFilesDao.getSourceFiles();
    }
    public List<SourceFile> getAvailableFileNames() throws SQLException {
        return sourceFileMonitorDao.getAvailableFiles();
    }
    public void updateAvailableFiles(List<SourceFile> sourceFiles) throws SQLException {
        sourceFileMonitorDao.updateAvailableFiles(sourceFiles);
    }

    public Health getHealth() {
        return health;
    }

    public List<SourceFile> scanFiles(List<SourceFile> sourceFiles, List<SourceFile> fileNames) throws IOException, NoSuchAlgorithmException {
        List<SourceFile> currentSourceFiles = new ArrayList<>();
         for(SourceFile s : sourceFiles) {
             File filePath = new File(s.getFilePath());
             if (!filePath.exists())
                 filePath.mkdirs();

             File[] files = filePath.listFiles((dir, name) -> name.matches(s.getFileName().replace('?', '.')));
            if(files == null)
                continue;
             for(File newFile : files){
                 SourceFile sourceFileMonitor = new SourceFile();
                 sourceFileMonitor.setFileId(s.getFileId());
                 sourceFileMonitor.setFileCode(s.getFileCode());
                 sourceFileMonitor.setFileName(s.getFileName());
                 sourceFileMonitor.setFilePath(s.getFilePath());
                 sourceFileMonitor.setFilePattern(s.getFilePattern());
                 sourceFileMonitor.setContact(s.getContact());
                 sourceFileMonitor.setEnabled(s.getEnabled());
                 sourceFileMonitor.setFileLocation(newFile.getAbsolutePath());
                 sourceFileMonitor.setAvailable(true);
                 sourceFileMonitor.setReadable(newFile.canRead());
                 sourceFileMonitor.setWritable(newFile.canWrite());
                 sourceFileMonitor.setModifiedDate(new Date(newFile.lastModified()));
                 sourceFileMonitor.setModified_by("Unknown"); //TODO: get the user who modified the file
                 sourceFileMonitor.setAvailableFrom(new Date());
                 sourceFileMonitor.setFileName(newFile.getName());
                 if(fileNames.stream().noneMatch(fName -> fName.getFileName().equals(sourceFileMonitor.getFileName()) && sourceFileMonitor.getModifiedDate()!= null &&fName.getModifiedDate().equals(sourceFileMonitor.getModifiedDate())))
                     sourceFileMonitor.setFileuuid(generateUUID(newFile));
                 sourceFileMonitor.setSize(newFile.length());
                 currentSourceFiles.add(sourceFileMonitor);
             }
         }
        return currentSourceFiles;

    }

    private static UUID generateUUID(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md;
        try( FileInputStream inputStream = new FileInputStream(file)){
            md = MessageDigest.getInstance("MD5");
            FileChannel channel = inputStream.getChannel();
            ByteBuffer buff = ByteBuffer.allocate(2048);
            while(channel.read(buff) != -1)
            {
                buff.flip();
                md.update(buff);
                buff.clear();
            }
            byte[] hashValue = md.digest();
            return UUID.nameUUIDFromBytes(hashValue);

        }
    }


    @Override
    public void run() {
        try {
            logger.info("Source file monitor started");
            List<SourceFile> sourceFiles = getFileList();
            List<SourceFile> availableFiles = getAvailableFileNames();
            sourceFiles = scanFiles(sourceFiles, availableFiles);
            updateAvailableFiles(sourceFiles);
            logger.info("Source file monitor Completed");
            health.setAvailable(true);
            health.setAction("No action required");
            health.setMessage("Running fine");
        } catch (Exception e) {
            logger.error("Error in Source file monitor", e);
            health.setAvailable(false);
            health.setAction("Please check log to check error for Source file monitor");
            health.setMessage("Source file monitor unhealthy");
        }
            health.setLastChecked(new Date());

    }

}
