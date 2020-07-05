import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class InvertedIndexMapper extends Mapper<Object, Text, Object, Text> {
    private final Text keyInfo = new Text();
    private final Text valueInfo = new Text();
    private final Set<String> patternsToSkip = new HashSet<>();
    private String currentFile = "";
    private Integer rowCount = 0;

    @Override
    public void setup(Context context) throws IOException {
        Configuration conf = context.getConfiguration();
        if (conf.getBoolean("inverted.skip.patterns", false)) {
            URI[] patternsURIs = Job.getInstance(conf).getCacheFiles();
            for (URI patternsURI : patternsURIs) {
                Path patternsPath = new Path(patternsURI.getPath());
                String patternsFileName = patternsPath.getName();
                parseSkipFile(patternsFileName);
            }
        }
    }

    private void parseSkipFile(String fileName) {
        try {
            BufferedReader fis = new BufferedReader(new FileReader(fileName));
            String pattern;
            while ((pattern = fis.readLine()) != null) {
                patternsToSkip.add(pattern);
            }
        } catch (IOException ioe) {
            System.err.println("Caught exception while parsing the cached file '"
                    + StringUtils.stringifyException(ioe));
        }
    }

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        // Use split to get the current chunk name
        FileSplit split = (FileSplit) context.getInputSplit();
        String newFile = split.getPath().getName();

        // Split input by end of line
        StringTokenizer itr1 = new StringTokenizer(value.toString(), "\n");

        while (itr1.hasMoreTokens()) {

            if (newFile.equals(currentFile)) {
                rowCount++;
            } else {
                rowCount = 1;
                currentFile = newFile;
            }

            String line = itr1.nextToken();
            // Split input by separators
            StringTokenizer itr2 = new StringTokenizer(line, "\"',.()?![]#$*-;:_+/\\<>@%& ");

            while (itr2.hasMoreTokens()) {
                String word = itr2.nextToken();
                if (!patternsToSkip.contains(word)) {
                    // format: (word:file, value)
                    keyInfo.set(word.toLowerCase() + ":" + newFile);
                    valueInfo.set(rowCount.toString());
                    context.write(keyInfo, valueInfo);
                }
            }
        }
    }
}