package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.WindowConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.io.*;
import java.util.List;

public class TableApp extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(TableApp.class);

    private final JTable table;
    private final MyTableModel tableModel;

    public TableApp() {
        setTitle("Table Application");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        tableModel = new MyTableModel();
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        setRowColors();

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton addButton = new JButton("Add Row");
        JButton deleteButton = new JButton("Delete Row");
        JButton saveButton = new JButton("Save to File");
        JButton loadButton = new JButton("Load from File");
        JButton pdfButton = new JButton("Export to PDF");
        JButton printButton = new JButton("Print");

        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(saveButton);
        panel.add(loadButton);
        panel.add(pdfButton);
        panel.add(printButton);

        add(panel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> tableModel.addRow());
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            }
        });

        saveButton.addActionListener(this::saveToFile);
        loadButton.addActionListener(this::loadFromFile);
        pdfButton.addActionListener(this::exportToPDF);
        printButton.addActionListener(this::printTable);

        setVisible(true);
    }

    private void setRowColors() {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
                }
                return c;
            }
        });
    }

    private void saveToFile(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, tableModel.getData());
            } catch (IOException e) {
                logger.error("Error saving to file", e);
            }
        }
    }

    private void loadFromFile(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<List<Object>> data = mapper.readValue(file, TypeFactory.defaultInstance().constructCollectionType(List.class, TypeFactory.defaultInstance().constructCollectionType(List.class, Object.class)));
                tableModel.setData(data);
            } catch (IOException e) {
                logger.error("Error loading from file", e);
            }
        }
    }

    private void exportToPDF(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Ensure the file has a .pdf extension
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }

            Document document = new Document();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                PdfWriter writer = PdfWriter.getInstance(document, fos);
                document.open();

                PdfPTable pdfTable = new PdfPTable(tableModel.getColumnCount());

                // Add table headers
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(tableModel.getColumnName(i)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }
                pdfTable.setHeaderRows(1);

                // Add table data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(tableModel.getValueAt(i, j).toString()));
                        cell.setHorizontalAlignment(getAlignmentForColumn(j));
                        pdfTable.addCell(cell);
                    }
                }

                document.add(pdfTable);
                logger.info("PDF exported successfully to " + file.getAbsolutePath());
            } catch (DocumentException | IOException e) {
                logger.error("Error exporting to PDF", e);
            } finally {
                if (document.isOpen()) {
                    document.close();
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        logger.error("Error closing FileOutputStream", e);
                    }
                }
            }
        }
    }

    private int getAlignmentForColumn(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Element.ALIGN_LEFT;
            case 1:
                return Element.ALIGN_CENTER;
            case 2:
                return Element.ALIGN_RIGHT;
            default:
                return Element.ALIGN_LEFT;
        }
    }

    private void printTable(ActionEvent event) {
        try {
            boolean complete = table.print();
            if (complete) {
                JOptionPane.showMessageDialog(this, "Printing Complete", "Print Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Printing Cancelled", "Print Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException e) {
            logger.error("Error printing table", e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Print Result", JOptionPane.ERROR_MESSAGE);
        }
    }

}
