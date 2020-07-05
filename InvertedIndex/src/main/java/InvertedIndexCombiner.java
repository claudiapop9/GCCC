import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class InvertedIndexCombiner extends Reducer<Text, Text, Text, Text> {
    private final Text info = new Text();

    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        // format: (word, (file, line, line...))
        StringBuilder lines = new StringBuilder();
        for (Text value : values) {
            lines.append(value.toString()).append(", ");
        }

        lines = new StringBuilder(lines.substring(0, lines.length() - 2)); // Remove last ", "

        String[] keySplit = key.toString().split(":");
        String word = keySplit[0];
        String file = keySplit[1];

        info.set("(" + file + ", " + lines + ")");
        key.set(word);
        context.write(key, info);
    }
}