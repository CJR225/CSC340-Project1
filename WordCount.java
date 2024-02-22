import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class WordCount {
    /*
     * @param path path to the file
     * 
     * @return total number of words in the file references by path
     * 
     * @throws FileNotFoundException if file doesn't exist
     * 
     * @precondition file must be a valid text file
     * 
     * @postcondition wordCount represents total number of words separated by space
     */
    public static int wordCount(String path) throws FileNotFoundException {
        // File object
        File file = new File(path);

        // file existence check
        if (!file.exists())
            throw new FileNotFoundException();

        Scanner reader = new Scanner(file);

        int wordCount = 0;

        // 1. read file line by line, count # of words, accumulate result
        // 2. this approach is faster for large file, limits stack overflow error
        while (reader.hasNext())
            wordCount += reader.nextLine().trim().split("\\s+").length;

        reader.close();
        return wordCount;
    }

    public static void main(String[] args) {
        try {
            System.out.println(wordCount("words.txt"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}