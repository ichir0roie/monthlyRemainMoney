package ichir0roie.mine.monthlyremainmoney;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class OCR {
    String tessDataDir="tessdata"+ File.separator;
    String[] tessTrainedData=new String[]{
            "eng.traineddata",
            "jpn.traineddata",
            "test.txt"
    };

    public OCR(Context context){
        baseAPI=new TessBaseAPI();
        try {
            fileSetup(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    TessBaseAPI baseAPI;
    public String getText(Context context, Bitmap bitmap) {
        String tesbuf=context.getFilesDir().toString();
        baseAPI.init(context.getFilesDir().toString(), "eng");
        baseAPI.setImage(bitmap);
        String getText = baseAPI.getUTF8Text();
        baseAPI.end();
        return getText;
    }

    public void fileSetup(Context context) throws IOException {
        String filePath=context.getFilesDir().toString()+File.separator+ tessDataDir+tessTrainedData[2];

        String testex=tessDataDir+tessTrainedData[2];
        AssetManager assetManager= context.getAssets();
        InputStream inputStream=  assetManager.open(tessDataDir+tessTrainedData[2]);
        BufferedReader reader=new BufferedReader(
                new InputStreamReader(inputStream,StandardCharsets.UTF_8)
        );

        File file=new File(context.getFilesDir(),tessDataDir+tessTrainedData[2]);


        FileOutputStream out=new FileOutputStream(file.toString());

        String lineBuffer;
        String text="";
        while ((lineBuffer=reader.readLine())!=null) {
            text += lineBuffer;
            out.write(lineBuffer.getBytes());
        }

        out.close();

//        if (out != null) {
//            out.write(text.getBytes());
//        }
//        inputStream.close();
//        if (out != null) {
//            out.close();
//        }
        reader.close();

    }
}
