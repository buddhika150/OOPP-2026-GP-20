package ftms.gui;

import ftms.model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Java Swing GUI Panel for viewing notices.
 * Assigned to Member 3 as per SRS 2.3, 9.4.
 */
public class NoticeViewPanel extends JPanel {
    private JTable noticeTable;
    private DefaultTableModel tableModel;
    private JTextArea txtNoticeContent;

    public NoticeViewPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        loadNotices();
    }

    private void initComponents() {
        // TOP Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Faculty Notices & Announcements");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Refresh Notices");
        pnlHeader.add(btnRefresh, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // CENTER: SplitPane with list of notices on top/left and content on bottom/right
        String[] columns = {"ID", "Title", "Published Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        noticeTable = new JTable(tableModel);
        noticeTable.setRowHeight(24);
        noticeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTable = new JScrollPane(noticeTable);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Notice Board"));

        txtNoticeContent = new JTextArea();
        txtNoticeContent.setEditable(false);
        txtNoticeContent.setLineWrap(true);
        txtNoticeContent.setWrapStyleWord(true);
        txtNoticeContent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNoticeContent.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollContent = new JScrollPane(txtNoticeContent);
        scrollContent.setBorder(BorderFactory.createTitledBorder("Notice Content"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTable, scrollContent);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);

        // Listeners
        btnRefresh.addActionListener(e -> loadNotices());
        noticeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && noticeTable.getSelectedRow() != -1) {
                int row = noticeTable.getSelectedRow();
                String title = (String) tableModel.getValueAt(row, 1);
                txtNoticeContent.setText("Selected Notice: " + title + "\n\n(Click on any notice to view full details)");
            }
        });
    }

    private void loadNotices() {
        tableModel.setRowCount(0);
        txtNoticeContent.setText("");
        try {
            List<TechnicalOfficer.NoticeEntry> list = TechnicalOfficer.viewNotices();
            for (TechnicalOfficer.NoticeEntry n : list) {
                tableModel.addRow(new Object[]{
                        n.getNoticeId(),
                        n.getTitle(),
                        n.getPublishedDate()
                });
            }

            noticeTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && noticeTable.getSelectedRow() != -1) {
                    int row = noticeTable.getSelectedRow();
                    int id = (int) tableModel.getValueAt(row, 0);
                    for (TechnicalOfficer.NoticeEntry n : list) {
                        if (n.getNoticeId() == id) {
                            txtNoticeContent.setText("Title: " + n.getTitle() +
                                    "\nDate: " + n.getPublishedDate() +
                                    "\n\nContent:\n" + (n.getContent() != null ? n.getContent() : "No content"));
                            break;
                        }
                    }
                }
            });
        } catch (SQLException ex) {
            UiUtils.showError(this, "Error loading notices: " + ex.getMessage(), "Database Error");
        }
    }
}
