private static boolean compareManifestFiles(String filePath1, String filePath2) {
    try {
        BufferedReader reader1 = new BufferedReader(new FileReader(filePath1));
        BufferedReader reader2 = new BufferedReader(new FileReader(filePath2));
        String line1;
        String line2;

        while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null) {
            if (!line1.equals(line2)) {
                // Manifest files are different
                reader1.close();
                reader2.close();
                return false;
            }
        }

        // Make sure both files reached the end
        if (line1 != null || line2 != null) {
            reader1.close();
            reader2.close();
            return false;
        }

        reader1.close();
        reader2.close();
        return true; // Manifest files are identical
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}
