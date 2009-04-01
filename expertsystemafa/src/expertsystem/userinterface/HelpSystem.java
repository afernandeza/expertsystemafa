package expertsystem.userinterface;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

public class HelpSystem extends JFrame implements HyperlinkListener{
    
    static final long serialVersionUID = -9877896;
    
    public HelpSystem(String index, int width, int height){
        super("Ayuda: JExpert");
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(this);
        
        URL url = getClass().getResource(index);
        if(url != null){
            try{
                editorPane.setPage(url);
            } catch(IOException ioe){
                System.err.println("Bad URL: " + url);
            }
        } else {
            System.err.println("File not found: " + index);
        }
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setPreferredSize(new Dimension(width, height));
        editorScrollPane.setMinimumSize(new Dimension(10, 10));
        this.add(editorScrollPane);
        this.setIcon(this);
        this.pack();
        this.setSize(new Dimension(this.getWidth() + 10, this.getHeight() + 10));
        this.setLocationRelativeTo(null);
    }
    
    public void hyperlinkUpdate(HyperlinkEvent e){
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            JEditorPane pane = (JEditorPane) e.getSource();
            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                HTMLDocument doc = (HTMLDocument)pane.getDocument();
                doc.processHTMLFrameHyperlinkEvent(evt);
            } else {
                try {
                    pane.setPage(e.getURL());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }
    
    private void setIcon(JFrame jFrame) {
        String path = "images/brain.gif";
        try {
            jFrame.setIconImage(this.createImage(path).getImage());
        } catch (Exception e) {
        }
    }
    
    private ImageIcon createImage(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + imgURL);
            return null;
        }
    }

}
