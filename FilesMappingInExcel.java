import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FilesMappingInExcel1 {

    public static void main(String[] args) throws WriteException, IOException {
        List<String> sourceRepoList = new ArrayList<>();
        Map<String, List<String>> destinationReposFileMap = new HashMap<>();
        Map<String, List<String>> destinationReposLineMap = new HashMap<>();
        List<String> repoFileNames = new ArrayList<>();

        WritableWorkbook workbook = null;


        try {
            BufferedReader fileListReader = new BufferedReader(new FileReader("D:\\SPRING\\Samp\\path_RR.txt"));
            String repoFilePath;
            while ((repoFilePath = fileListReader.readLine()) != null) {
                sourceRepoList.add(repoFilePath);
            }
            fileListReader.close();
            BufferedReader repoReader = new BufferedReader(new FileReader("D:\\SPRING\\Samp\\path_RD.txt"));
            String repoLine;
            while ((repoLine = repoReader.readLine()) != null) {
                readRepoFileAndPopulateMaps(destinationReposFileMap,destinationReposLineMap, repoFileNames, repoLine);
            }
            repoReader.close();

            workbook = Workbook.createWorkbook(new File("D:\\SPRING\\Samp\\output.xls"));
            reconFile(sourceRepoList, destinationReposFileMap,destinationReposLineMap, workbook);
            findDuplicate(destinationReposFileMap,destinationReposLineMap, workbook);
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook.close();
        }
    }
    private static void reconFile(List<String> SourceRepoList, Map<String, List<String>> destinationReposFileMap,Map<String, List<String>> destinationReposLineMap, WritableWorkbook workbook) {
        try {
            for (String sourceRepo : SourceRepoList) {
                String sheetName = sourceRepo.substring(sourceRepo.lastIndexOf("\\") + 1, sourceRepo.lastIndexOf("."));
                WritableSheet sheet = workbook.createSheet(sheetName, workbook.getNumberOfSheets());
                BufferedReader reader = new BufferedReader(new FileReader(sourceRepo));
                String line;
                int rowIndex = 1;
                Label headerLabelA = new Label(0, 0, "FileSourcePath");
                Label headerLabelB = new Label(1, 0, "MappedRepo");
                Label headerLabelC = new Label(2, 0, "ComparedStructure");
                sheet.addCell(headerLabelA);
                sheet.addCell(headerLabelB);
                sheet.addCell(headerLabelC);

                while ((line = reader.readLine()) != null) {
                    String key = null;
                    String fullPath = line.trim().replace("\\", "/");
                    key = getFilewithPathSource(line);
//                    System.out.println("Parent: " + fullPath);
//                    System.out.println("Parts: " + key);
                    List<String> movedToRepos = destinationReposFileMap.getOrDefault(key, new ArrayList<>());
                    List<String> movedToReposPath = destinationReposLineMap.getOrDefault(key, new ArrayList<>());

//                    System.out.println(movedToRepos);
                    writeExcel(rowIndex++, fullPath, String.join(",", movedToRepos), fullPath, sheet, key,movedToReposPath);

                }
                reader.close();
                System.out.println("Excel file created successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void findDuplicate( Map<String, List<String>> destinationReposFileMap,Map<String, List<String>> destinationReposLineMap, WritableWorkbook workbook) {
        try {
            String key = null;
            String sheetName = "DuplicateFileList";
            WritableSheet sheet = workbook.createSheet(sheetName, workbook.getNumberOfSheets());
            int rowIndex = 1;
            Label headerLabelA = new Label(0, 0, "FileSourcePath");
            Label headerLabelB = new Label(1, 0, "MappedRepo");
            Label headerLabelC = new Label(2, 0, "ComparedStructure");
            Label headerLabelD = new Label(3, 0, "DestinationPath");
            sheet.addCell(headerLabelA);
            sheet.addCell(headerLabelB);
            sheet.addCell(headerLabelC);
            sheet.addCell(headerLabelD);
            List<String> movedToRepos = null;
            for (Map.Entry<String, List<String>> entry : destinationReposFileMap.entrySet()) {
                key = entry.getKey();
                movedToRepos = entry.getValue();
                List<String> movedToReposPath = destinationReposLineMap.getOrDefault(key, new ArrayList<>());
                if (movedToRepos.size() >= 2) {
                    writeExcel(rowIndex++, key, String.join(",", movedToRepos), key, sheet, key,movedToReposPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFilewithPathSource(String line) {
        String key = null;
        String fullPath = line.trim().replace("\\", "/");
        String[] parts = fullPath.split("/");
        StringBuilder res = new StringBuilder();
        if (parts.length > 0) {
            for (String part : parts) {
                if (!part.contains(".") && (part.equals("ejbModule") || part.startsWith("eBBS_") ||
                        part.startsWith("eGLEX_") || part.equals("Module") || part.equals("JavaSource") ||
                        part.startsWith("eDEPOSITS_") || part.endsWith(" eDEPOSITS") || part.startsWith("SCB ") ||
                        part.equals("Source") || part.equals("java") || part.equals("eGLEX") || part.equals("XMLS") ||
                        part.equals("source") || part.equals("resources") || part.equals("webapp") ||
                        part.equals("WebContent") || part.startsWith("eBRANCH_"))) {
                    res.delete(0, res.length());
                } else {
                    res.append("/").append(part);
                }
            }
            List<String> stringList = Arrays.asList(parts);
            key = res.toString();
            if (key.contains("META-INF") && (stringList.contains("src") || stringList.contains("ejbModule"))) {
                int indexOfSrc = stringList.indexOf("src");
                int indexOfEjbModule = stringList.indexOf("ejbModule");

                if (indexOfSrc != -1) {
                    // "src" exists, append the immediate part before it
                    key = parts[indexOfSrc - 1] + key;
                } else if (indexOfEjbModule != -1) {
                    // "ejbModule" exists, append the immediate part before it
                    key = parts[indexOfEjbModule - 1] + key;
                }
            }

        }
        return key;
    }

    private static void readRepoFileAndPopulateMaps(Map<String, List<String>> destinationReposRepoMap, Map<String, List<String>> destinationReposLineMap, List<String> destinationRepoName, String destinationRepoFileName) {
        try {
            String repo = destinationRepoFileName.substring(destinationRepoFileName.lastIndexOf("\\")+1);
            BufferedReader reader = new BufferedReader(new FileReader(destinationRepoFileName));
            reader.close();
            destinationRepoName.add(repo);

            reader = new BufferedReader(new FileReader(destinationRepoFileName));
            String line;
            while ((line = reader.readLine())!=null){
                String key = getFilewithPathSource(line);

                // Add 'repo' to the repo map
                destinationReposRepoMap.computeIfAbsent(key, k -> new ArrayList<>()).add(repo);

                // Add 'line' to the line map
                destinationReposLineMap.computeIfAbsent(key, k -> new ArrayList<>()).add(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void writeExcel(int rowindex, String keyFileName, String repoName,String fullPath, WritableSheet sheet, String key,List<String> repoPaths) throws RowsExceededException, WriteException {
        Label label0 = new Label(0, rowindex, keyFileName);
        Label label1 = new Label(2, rowindex, key);
        Label label2 = new Label(1, rowindex, repoName);

        int columnIndex = 3;
        for (String repoPath : repoPaths) {
            Label repoPathLabel = new Label(columnIndex, rowindex, repoPath);
            sheet.addCell(repoPathLabel);
            columnIndex++;
        }

        sheet.addCell(label0);
        sheet.addCell(label2);
        sheet.addCell(label1);


    }
}
