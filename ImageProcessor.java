import java.io.*;
import java.util.*;

public class ImageProcessor {
    
    public static void main(String[] args) {
        
    Scanner input = new Scanner(System.in);
    System.out.print("Enter the name of the PGM file: ");
    String fileName = input.nextLine();
    
    // Open the file
    File file = new File(fileName);
    if (!file.exists()) {
        System.out.println("File does not exist.");
        return;
    }
    
    try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
        String header = "";
        int width = 0;
        int height = 0;
        int maxVal = 0;
        byte[] pixels = null;
        boolean binary = false;
        
        // Read the PGM header
        header = readLine(in);
        if (!header.equals("P5")) {
            System.out.println("Invalid file format.");
            return;
        }
        width = readInt(in);
        height = readInt(in);
        maxVal = readInt(in);
        if (maxVal != 255) {
            System.out.println("Invalid maximum value.");
            return;
        }
        binary = true;
        
        // Read the pixels
        pixels = new byte[width * height];
        in.readFully(pixels);
        
        // Apply median filter
        byte[] medianPixels = medianFilter(pixels, width, height);
        
        // Write median filtered image
        writeImage("median.pgm", medianPixels, width, height, binary);
        
        // Apply average filter
        byte[] avgPixels = averageFilter(pixels, width, height);
        
        // Write average filtered image
        writeImage("average.pgm", avgPixels, width, height, binary);
        
        // Apply edge detection
        byte[] edgePixels = edgeDetection(pixels, width, height);
        
        // Write edge detected image
        writeImage("edge.pgm", edgePixels, width, height, binary);
        
        System.out.println("Done.");
    } catch (IOException e) {
        System.out.println("Error reading file.");
        e.printStackTrace();
    }
}

// Reads a line from the input stream
// Reads a line from the input stream
private static String readLine(DataInputStream in) throws IOException {
    StringBuilder sb = new StringBuilder();
    char c = (char)in.readByte();
    while (c == '#') { // Ignore comments
        while (c != '\n') {
            c = (char)in.readByte();
        }
        c = (char)in.readByte();
    }
    while (c != '\n') {
        sb.append(c);
        c = (char)in.readByte();
    }
    return sb.toString().trim();
}

// Reads an integer from the input stream
private static int readInt(DataInputStream in) throws IOException {
    StringBuilder sb = new StringBuilder();
    char c = (char)in.readByte();
    while (c == '#') { // Ignore comments
        while (c != '\n') {
            c = (char)in.readByte();
        }
        c = (char)in.readByte();
    }
    while (Character.isWhitespace(c)) {
        c = (char)in.readByte();
    }
    while (Character.isDigit(c)) {
        sb.append(c);
        c = (char)in.readByte();
    }
    return Integer.parseInt(sb.toString());
}

// Applies a median filter to the pixels
private static byte[] medianFilter(byte[] pixels, int width, int height) {
    byte[] result = new byte[pixels.length];
    int[] values = new int[9];
    for (int y = 1; y < height - 1; y++) {
        for (int x = 1; x < width - 1; x++) {
            int index = y * width + x;
            values[0] = pixels[index - width - 1] & 0xff;
            values[1] = pixels[index - width] & 0xff;
            values[2] = pixels[index - width + 1] & 0xff;
            values[3] = pixels[index - 1] & 0xff;
            values[4] = pixels[index] & 0xff;
            values[5] = pixels[index + 1] & 0xff;
            values[6] = pixels[index + width - 1] & 0xff;
            values[7] = pixels[index + width] & 0xff;
            values[8] = pixels[index + width + 1] & 0xff;
            
            // Sort the values
            Arrays.sort(values);
            
            // Set the median value as the new pixel value
            result[index] = (byte) values[4];
        }
    }
    return result;
}

// Applies an average filter to the pixels
private static byte[] averageFilter(byte[] pixels, int width, int height) {
    byte[] result = new byte[pixels.length];
    for (int y = 1; y < height - 1; y++) {
        for (int x = 1; x < width - 1; x++) {
            int index = y * width + x;
            int sum = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    sum += pixels[index + i * width + j] & 0xff;
                }
            }
            result[index] = (byte) (sum / 9);
        }
    }
    return result;
}

// Applies an edge detection to the pixels
private static byte[] edgeDetection(byte[] pixels, int width, int height) {
    byte[] result = new byte[pixels.length];
    int[] gx = {-1, 0, 1, -2, 0, 2, -1, 0, 1};  // Sobel operator
    int[] gy = {-1, -2, -1, 0, 0, 0, 1, 2, 1};  // Sobel operator
    for (int y = 1; y < height - 1; y++) {
        for (int x = 1; x < width - 1; x++) {
            int index = y * width + x;
            int gradX = 0, gradY = 0;
            int k = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int val = pixels[index + i * width + j] & 0xff;
                    gradX += gx[k] * val;
                    gradY += gy[k] * val;
                    k++;
                }
            }
            int grad = (int) Math.sqrt(gradX * gradX + gradY * gradY);
            result[index] = (byte) Math.min(grad, 255);
        }
    }
    return result;
}

// Writes an image to a file
private static void writeImage(String filename, byte[] pixels, int width, int height, boolean binary) throws IOException {
    try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
        out.writeBytes("P5\n");
        out.writeBytes(width + " " + height + "\n");
        out.writeBytes("255\n");
        out.write(pixels);
    }
}

          
}