package com.shopping.controller;

import com.shopping.dao.OrderDao;
import com.shopping.dao.ProductDao;
import com.shopping.dao.impl.OrderDaoImpl;
import com.shopping.dao.impl.ProductDaoImpl;
import com.shopping.model.Order;
import com.shopping.model.OrderDetail;
import com.shopping.model.Product;
import com.shopping.model.User;
import com.shopping.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class ShoppingView extends JFrame {

    // 介面元件
    private JTable table;
    private DefaultTableModel model;
    private JTextArea cartArea;
    private JLabel lblTotal;
    
    // 資料庫與邏輯物件
    private ProductDao productDao = new ProductDaoImpl();
    private OrderDao orderDao = new OrderDaoImpl();
    private Order currentOrder;
    private double currentTotal = 0.0;

    public ShoppingView() {
        // 初始化
        currentOrder = new Order("訪客", 0.0);

        setTitle("前台 - POS 收銀模擬系統");
        setBounds(100, 100, 800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // === 登出按鈕 ===
        JButton btnLogout = new JButton("登出");
        btnLogout.setBounds(680, 10, 80, 25);
        btnLogout.setBackground(Color.PINK);
        add(btnLogout);

        btnLogout.addActionListener(e -> {
            UserSession.setUser(null);
            dispose();
            new LoginView().setVisible(true);
        });

        // === 左邊：商品列表 ===
        JLabel lblList = new JLabel("商品列表");
        lblList.setBounds(20, 10, 200, 20);
        add(lblList);

        String[] cols = {"ID", "品名", "價格", "庫存", "描述"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 40, 450, 400);
        add(scroll);

        // === 右邊：購物清單 ===
        JLabel lblCart = new JLabel("購物車:");
        lblCart.setBounds(500, 10, 200, 20);
        add(lblCart);

        cartArea = new JTextArea();
        cartArea.setEditable(false);
        JScrollPane cartScroll = new JScrollPane(cartArea);
        cartScroll.setBounds(500, 40, 250, 350);
        add(cartScroll);

        lblTotal = new JLabel("總金額: $0.0");
        lblTotal.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        lblTotal.setForeground(Color.RED);
        lblTotal.setBounds(500, 400, 250, 30);
        add(lblTotal);

        JButton btnAdd = new JButton("加入購物車");
        btnAdd.setBounds(20, 460, 120, 40);
        add(btnAdd);

        JButton btnCheckout = new JButton("結帳 (選擇付款)");
        btnCheckout.setBounds(500, 460, 150, 40);
        btnCheckout.setBackground(Color.ORANGE);
        add(btnCheckout);

        // === 載入資料 ===
        loadData();

        // === 事件綁定 ===
        btnAdd.addActionListener(e -> addToCart());
        btnCheckout.addActionListener(e -> processPayment());
    }

    private void addToCart() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "請先選擇左邊的商品！");
            return;
        }

        int pid = Integer.parseInt(model.getValueAt(row, 0).toString());
        String pname = model.getValueAt(row, 1).toString();
        double pprice = Double.parseDouble(model.getValueAt(row, 2).toString());
        int pstock = Integer.parseInt(model.getValueAt(row, 3).toString());

        if (pstock <= 0) {
            JOptionPane.showMessageDialog(this, "此商品已售完！");
            return;
        }

        String qtyStr = JOptionPane.showInputDialog(this, "請輸入數量 (庫存:" + pstock + "):", "1");
        if (qtyStr == null) return;

        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty > 0 && qty <= pstock) {
                OrderDetail item = new OrderDetail();
                item.setProductId(pid);
                item.setQuantity(qty);
                item.setUnitPrice(pprice);
                
                currentOrder.addDetail(item);
                currentTotal += (pprice * qty);
                cartArea.append(pname + " x " + qty + " = $" + (pprice * qty) + "\n");
                lblTotal.setText("總金額: $" + currentTotal);
            } else {
                JOptionPane.showMessageDialog(this, "數量錯誤");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "請輸入數字");
        }
    }

    // ★★★ 核心功能：付款處理流程 ★★★
    private void processPayment() {
        if (currentOrder.getDetails().isEmpty()) {
            JOptionPane.showMessageDialog(this, "購物車是空的！");
            return;
        }

        String[] options = {"Apple Pay", "LINE Pay", "信用卡", "賒帳", "現金"};
        
        int choice = JOptionPane.showOptionDialog(
            this,
            "總金額：$" + currentTotal + "\n請選擇付款方式：",
            "結帳中心",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == -1) return;

        String paymentMethod = options[choice];

        switch (paymentMethod) {
            case "Apple Pay":
            case "LINE Pay":
                // ★ 呼叫 QR Code 模擬器
                simulateQRCode(paymentMethod);
                break;

            case "信用卡":
                // ★ 呼叫 信用卡感應 模擬器
                simulateCreditCard();
                break;

            case "賒帳":
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "<html><body><h2 style='color:red'>⚠️ 賒帳警告</h2>" +
                    "您選擇了「賒帳」模式。<br>" +
                    "根據規定，將收取 <b>10分利 (10%利率)</b>，按日增加！<br>" +
                    "是否確定？</body></html>",
                    "高利貸警告",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    finishOrder("賒帳 (10分利)", 0);
                }
                break;

            case "現金":
                handleCashPayment();
                break;
        }
    }

    // ★★★ 新功能：QR Code 掃描模擬器 ★★★
    private void simulateQRCode(String type) {
        // 建立一個彈出視窗 (Modal Dialog)
        JDialog dialog = new JDialog(this, type + " 支付", true);
        dialog.setSize(300, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this); // 置中

        // 1. 上方標題
        JLabel lblTitle = new JLabel("請掃描 QR Code 付款", SwingConstants.CENTER);
        lblTitle.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        dialog.add(lblTitle, BorderLayout.NORTH);

        // 2. 中間 QR Code 圖片 (使用 Google Chart API 產生一個假的 QR Code)
        try {
            // 這個網址會產生一個 QR Code 圖片
            String qrUrl = "https://chart.googleapis.com/chart?chs=200x200&cht=qr&chl=Payment_" + currentTotal;
            JLabel qrLabel = new JLabel(new ImageIcon(new URL(qrUrl)));
            dialog.add(qrLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            // 如果沒網路，顯示文字就好
            JLabel qrLabel = new JLabel("<< 這裡是 QR Code >>", SwingConstants.CENTER);
            qrLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            dialog.add(qrLabel, BorderLayout.CENTER);
        }

        // 3. 下方進度條 (模擬掃描過程)
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true); // 顯示 % 數
        progressBar.setString("等待掃描...");
        dialog.add(progressBar, BorderLayout.SOUTH);

        // 4. 使用 Timer 模擬 3 秒的掃描動畫
        Timer timer = new Timer(50, null); // 每 50毫秒跑一次
        final int[] progress = {0};

        timer.addActionListener(e -> {
            progress[0]++;
            progressBar.setValue(progress[0]);
            
            if (progress[0] < 50) {
                progressBar.setString("等待手機掃描...");
            } else if (progress[0] < 90) {
                progressBar.setString("交易處理中...");
            }

            if (progress[0] >= 100) {
                timer.stop();
                dialog.dispose(); // 關閉視窗
                // 跳出成功訊息
                JOptionPane.showMessageDialog(this, type + " 付款成功！");
                finishOrder(type, 0);
            }
        });
        
        timer.start(); // 開始倒數
        dialog.setVisible(true); // 顯示視窗 (程式會停在這裡直到 dialog 關閉)
    }

    // ★★★ 新功能：信用卡感應模擬器 ★★★
    private void simulateCreditCard() {
        JDialog dialog = new JDialog(this, "信用卡感應", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(3, 1));
        dialog.setLocationRelativeTo(this);

        JLabel lblIcon = new JLabel("💳 請將卡片靠近感應區", SwingConstants.CENTER);
        lblIcon.setFont(new Font("微軟正黑體", Font.BOLD, 18));
        dialog.add(lblIcon);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); // 來回跑的進度條 (因為不知道要感應多久)
        dialog.add(progressBar);
        
        JLabel lblStatus = new JLabel("準備中...", SwingConstants.CENTER);
        dialog.add(lblStatus);

        // 模擬感應延遲
        Timer timer = new Timer(1000, null); // 每一秒觸發一次
        final int[] step = {0};

        timer.addActionListener(e -> {
            step[0]++;
            if (step[0] == 1) {
                lblStatus.setText("嗶！感應成功，連線銀行中...");
                lblStatus.setForeground(Color.BLUE);
            } else if (step[0] == 2) {
                lblStatus.setText("授權通過！");
                lblStatus.setForeground(Color.GREEN.darker());
                progressBar.setIndeterminate(false);
                progressBar.setValue(100);
            } else if (step[0] == 3) {
                timer.stop();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "信用卡付款完成！");
                finishOrder("信用卡", 0);
            }
        });

        timer.start();
        dialog.setVisible(true);
    }

    // 現金付款邏輯
    private void handleCashPayment() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JSpinner spin1000 = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        JSpinner spin500 = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        JSpinner spin100 = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));

        panel.add(new JLabel("1000元 張數:"));
        panel.add(spin1000);
        panel.add(new JLabel("500元 張數:"));
        panel.add(spin500);
        panel.add(new JLabel("100元 張數:"));
        panel.add(spin100);
        
        int result = JOptionPane.showConfirmDialog(
            this, panel, "總額: $" + currentTotal, 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            int n1000 = (int) spin1000.getValue();
            int n500 = (int) spin500.getValue();
            int n100 = (int) spin100.getValue();
            double paid = (n1000 * 1000) + (n500 * 500) + (n100 * 100);
            double change = paid - currentTotal;

            if (change < 0) {
                JOptionPane.showMessageDialog(this, "金額不足！還差 $" + Math.abs(change));
            } else {
                JOptionPane.showMessageDialog(this, "找零: $" + change);
                finishOrder("現金", change);
            }
        }
    }

    // 完成訂單
    private void finishOrder(String paymentType, double change) {
        User currentUser = UserSession.getUser();
        String customerName = (currentUser != null) ? currentUser.getName() : "訪客";

        Order finalOrder = new Order(customerName, currentTotal);
        for (OrderDetail item : currentOrder.getDetails()) {
            finalOrder.addDetail(item);
        }

        try {
            orderDao.createOrder(finalOrder);
            String receipt = "訂單建立成功！\n客戶：" + customerName + 
                           "\n付款：" + paymentType + 
                           (change > 0 ? "\n找零：$" + change : "");
            JOptionPane.showMessageDialog(this, receipt);
            
            cartArea.setText("");
            lblTotal.setText("總金額: $0.0");
            currentTotal = 0;
            currentOrder = new Order(customerName, 0.0);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "錯誤：" + ex.getMessage());
        }
    }

    private void loadData() {
        model.setRowCount(0);
        List<Product> list = productDao.getAllProducts();
        for (Product p : list) {
            model.addRow(new Object[]{
                p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getDescription()
            });
        }
    }
}