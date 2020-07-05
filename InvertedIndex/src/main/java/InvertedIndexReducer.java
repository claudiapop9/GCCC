import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class InvertedIndexReducer
        extends Reducer<Text, Text, Text, Text> {

    private final Text result = new Text();

    protected void reduce(Text key, Iterable<Text> values,Context context)
            throws IOException, InterruptedException {
        StringBuilder fileList = new StringBuilder();
        for(Text value : values){
            fileList.append(value.toString()).append("; ");
        }
        result.set(fileList.toString());
        context.write(key, result);
    }
}