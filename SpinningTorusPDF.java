import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SpinningTorusPDF {

    public static void main(String[] args) {
        boolean running = true;

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            Frame frame = new Frame();
            frame.setSize(400, 200);
            frame.setTitle("Press Space to Start/Stop Animation");

            frame.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        running = !running;  // Toggle animation
                    } else if (e.getKeyCode() == KeyEvent.VK_S) {
                        System.exit(0);  // Stop program
                    }
                }

                public void keyReleased(KeyEvent e) {}
                public void keyTyped(KeyEvent e) {}
            });

            int width = 600;   // Width of the PDF in points (1 point = 1/72 inch)
            int height = 800;  // Height of the PDF

            for (int frameCount = 0; running; frameCount++) {
                StringBuilder sb = new StringBuilder();
                for (int y = -height / 20; y < height / 20; y++) {
                    for (int x = -width / 20; x < width / 20; x++) {
                        double theta = Math.atan2(y, x);
                        double phi = Math.atan2(Math.sqrt(x * x + y * y), 20.0);

                        double torusX = 15 * Math.cos(theta + frameCount * Math.PI / 180);
                        double torusY = 15 * Math.sin(theta + frameCount * Math.PI / 180);
                        double torusZ = 5 * Math.sin(phi);

                        if ((x - torusX) * (x - torusX) + (y - torusY) * (y - torusY) + (torusZ) * (torusZ) <= 1) {
                            sb.append('#');
                        } else {
                            sb.append(' ');
                        }
                    }
                    sb.append('\n');
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(50, height - 50);  // Position in PDF
                contentStream.showText(sb.toString());
                contentStream.endText();

                document.save("spinning_torus_page_" + frameCount + ".pdf");
                contentStream.close();

                try {
                    Thread.sleep(50);  // Delay for frame updates
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print("\033[H\033[2J");  // Clear terminal screen
            }

            contentStream.close();
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

