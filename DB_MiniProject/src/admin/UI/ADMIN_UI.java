package admin.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import DTO.RECOMMENDBOOKS; // RECOMMENDBOOKS DTO import
import DTO.RENTALS; // RENTALS DTO import
import DTO.RESERVATIONS;
import DTO.REVIEWS;
import DTO.USERS;
import admin.DAO.MemberManagementDAO;
import admin.DAO.RecommendBooksDAO;
import admin.DAO.RentalsDAO;
import admin.DAO.ReservationsDAO;
import admin.DAO.ReviewDao;
import admin.DAO.MemberManagementDAO;

public class ADMIN_UI extends JFrame {

    private RecommendBooksDAO recommendBooksDAO;
    private ReservationsDAO reservationsDAO;
    private RentalsDAO rentalsDAO;

	public ADMIN_UI() {
        recommendBooksDAO = new RecommendBooksDAO(); 
        reservationsDAO = new ReservationsDAO();
        rentalsDAO = new RentalsDAO();
        adminMainWindow(); // UI 설정 메서드 호출
    }

    // UI 구성 요소
    public void adminMainWindow() {
        setTitle("[관리자]즐거운 도서 생활");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 400);
        setLocationRelativeTo(null);

        // 로그아웃
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.setSize(1100, 50);
        
        JButton logout = new JButton("로그아웃");
        JLabel nameLabel = new JLabel("관리자님 환영합니다.");
        logoutPanel.add(nameLabel);
        logoutPanel.add(logout);

        logout.addActionListener(e -> {
        	try {
	            // 현재 실행 중인 JAR 또는 클래스 경로 가져오기
	            String javaHome = System.getProperty("java.home");
	            String javaBin = javaHome + "/bin/java";
	            String classPath = System.getProperty("java.class.path");
	            String mainClassName = "Main"; // default package에 있는 Main 클래스 이름
	            JOptionPane.showMessageDialog(this, "다음에 또 만나요~", "로그아웃", JOptionPane.INFORMATION_MESSAGE);
				
	            // 새 프로세스 실행 (default package에서 Main 클래스를 실행)
	            ProcessBuilder processBuilder = new ProcessBuilder(javaBin, "-cp", classPath, mainClassName);
	            processBuilder.start();

	            // 현재 애플리케이션 종료
	            System.exit(0);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
        });

        // 내부 패널 생성 (그리드 요소를 중앙 정렬)
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 30, 30)); // 2x3 배열, 간격 30px
        gridPanel.setSize(1100, 350);

        // 버튼 생성
        JButton bookInfoButton = new JButton("도서 정보 관리");
        JButton userInfoButton = new JButton("회원 정보 관리");
        JButton reviewButton = new JButton("리뷰 관리");
        JButton reservationButton = new JButton("예약 및 대여 관리");
        JButton recommendBookButton = new JButton("희망도서 신청 관리");
        JButton categoryButton = new JButton("카테고리 관리");

        bookInfoButton.setPreferredSize(new Dimension(300, 100));
        userInfoButton.setPreferredSize(new Dimension(300, 100));
        reviewButton.setPreferredSize(new Dimension(300, 100));
        reservationButton.setPreferredSize(new Dimension(300, 100));
        recommendBookButton.setPreferredSize(new Dimension(300, 100));
        categoryButton.setPreferredSize(new Dimension(300, 100));

        // 버튼을 그리드 패널에 추가
        gridPanel.add(bookInfoButton);
        gridPanel.add(userInfoButton);
        gridPanel.add(reviewButton);
        gridPanel.add(reservationButton);
        gridPanel.add(recommendBookButton);
        gridPanel.add(categoryButton);

        // 그리드 패널을 가운데 정렬하기 위한 감싸는 패널
        JPanel gridCenterPanel = new JPanel(new GridBagLayout()); // 중앙 정렬 레이아웃
        gridCenterPanel.add(gridPanel);

        // 버튼 동작 설정
        bookInfoButton.addActionListener(e -> showBookInfo());
//        userInfoButton.addActionListener(e -> showUserInfo());
        userInfoButton.addActionListener(e -> userInfo());
        reviewButton.addActionListener(e -> showReviews());
        reservationButton.addActionListener(e -> showReservationManagementWindow());
        recommendBookButton.addActionListener(e -> showRecommendBooksWindow());
        categoryButton.addActionListener(e -> showCategories());

        // 외부 패널 생성 및 레이아웃 설정
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.add(logoutPanel, BorderLayout.NORTH); // 로그아웃 패널 상단 추가
        outerPanel.add(gridCenterPanel, BorderLayout.CENTER); // 그리드 패널을 중앙에 추가

        // 외부 패널을 프레임에 추가
        add(outerPanel);
        setVisible(true);
        
    }
    private static class NonEditableTableModel extends DefaultTableModel {
        public NonEditableTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // 모든 셀이 수정 불가
        }

        // getValueAt 메서드 오버라이드 (필요한 경우 추가 변환 작업)
        @Override
        public Object getValueAt(int row, int column) {
            Object value = super.getValueAt(row, column);
            if (column == 7 && value instanceof String) { // "상태" 컬럼일 경우
                String status = (String) value;
                if ("Y".equals(status)) {
                    return "완료"; // "Y"를 "완료"로 변환
                } else if ("N".equals(status)) {
                    return "예약중"; // "N"을 "예약중"으로 변환
                }
            }
            return value; // 변환 조건에 해당하지 않으면 원래 값 반환
        }
    }

    public void userInfo() {
        JFrame UserListFrame = new JFrame("회원 목록");
        MemberManagementDAO memberDAO = new MemberManagementDAO();

        UserListFrame.setSize(800, 400);
        UserListFrame.setLocationRelativeTo(null); // 화면 가운데 위치
        UserListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel UserListPanel = new JPanel(new BorderLayout());

        // 테이블 모델 설정
        String[] columnNames = {"회원 ID", "이름", "연락처", "가입일", "대여 가능 여부", "연체 횟수"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 모든 셀을 비활성화
                return false;
            }
        };
        JTable reviewTable = new JTable(tableModel);

        // 데이터베이스에서 데이터를 가져와서 테이블에 추가
        List<USERS> users = memberDAO.getAllUsers();
        for (USERS user : users) {
            Object[] rowData = {
                user.getUserID(),
                user.getUserName(),
                user.getTel(),
                user.getRegdate(),
                user.getRentalYN(),
                user.getDelayCount()
            };
            tableModel.addRow(rowData);
        }

        // 테이블을 스크롤 팬에 추가
        JScrollPane scrollPane = new JScrollPane(reviewTable);
        UserListPanel.add(scrollPane, BorderLayout.CENTER);

        // 삭제 버튼 추가
        JButton deleteButton = new JButton("유저 삭제");
        deleteButton.addActionListener(e -> {
            int selectedRow = reviewTable.getSelectedRow();
            if (selectedRow != -1) {
                String userID = (String)tableModel.getValueAt(selectedRow, 0);
                if(userID.equals("admin")) {
                	JOptionPane.showMessageDialog(null, "관리자 계정은 삭제 불가합니다.", "관리자 계정 삭제 불가", JOptionPane.ERROR_MESSAGE);
        			return; // 메서드 종료
                }
                int confirm = JOptionPane.showConfirmDialog(UserListFrame, "유저를 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // 데이터베이스에서 삭제
                    memberDAO.deleteUser(userID);
                    // 테이블에서 삭제
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(UserListFrame, "유저가 삭제되었습니다.");
                }
            } else {
                JOptionPane.showMessageDialog(UserListFrame, "삭제할 유저를 선택하세요.");
            }
        });

        // 하단에 버튼 배치
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        UserListPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 패널을 프레임에 추가
        UserListFrame.add(UserListPanel);

        // 프레임 표시
        UserListFrame.setVisible(true);
    }

    // 리뷰를 조회하는 메서드
    private void showReviews() {
        // 리뷰를 보여주는 로직 작성
    	openAdminReviewUi();
    
    	List<REVIEWS> reviews = ReviewDao.getAllReviews();
    	StringBuilder reviewText = new StringBuilder("리뷰목록:\n");
    	
    	for (REVIEWS review : reviews) {
    		reviewText.append("리뷰 ID: ").append(review.getReviewID())
    		          .append(", 사용자 ID: ").append(review.getUserID())
                      .append(", 책 ID: ").append(review.getBookID())
                      .append(", 점수: ").append(review.getScore())
                      .append(", 내용: ").append(review.getReview())
                      .append(", 날짜: ").append(review.getReviewDate())
                      .append("\n");
    	}
    }
    
    private void openAdminReviewUi() {
		new AdminReviewUi();
		
	}

	//도서 정보를 조회하는 메서드
	private void showBookInfo() {
		new BookManagementFrame();
	}

	//회원 정보를 조회하는 메서드
    private void showUserInfo() {
        JOptionPane.showMessageDialog(this, "회원 정보를 조회합니다.");
    }

	// 예약 및 대여 관리 창을 보여주는 메서드
	private void showReservationManagementWindow() {
		JFrame reservationFrame = new JFrame("예약 및 대여 관리");
		reservationFrame.setSize(800, 600);
		reservationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        
        class NonEditableTableModel extends DefaultTableModel {
            public NonEditableTableModel(Object[] columnNames, int rowCount) {
                super(columnNames, rowCount);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 모든 셀이 수정 불가
            }

            // getValueAt 메서드를 오버라이드하여 "Y"와 "N"을 "완료"와 "예약중"으로 변환
            @Override
            public Object getValueAt(int row, int column) {
                Object value = super.getValueAt(row, column);
                if (column == 4 && value instanceof String) { // 상태 컬럼 (4번 열)에서 String 체크
                    String status = (String) value;
                    switch (status) {
                        case "Y":
                            return "완료"; // "Y"를 "완료"로 변환
                        case "N":
                            return "예약중"; // "N"을 "예약중"으로 변환
                        default:
                            return status; // 다른 값은 그대로 반환
                    }
                }
                return value; // 변환 조건이 없으면 원래 값 반환
            }
        }



        
        // 예약 관리 탭
        JPanel reservationPanel = new JPanel();
        reservationPanel.setLayout(new BorderLayout());
        String[] reservationColumnNames = { "ID", "회원 ID", "도서명", "예약일", "상태" };
        DefaultTableModel reservationModel = new NonEditableTableModel(reservationColumnNames, 0);
        JTable reservationTable = new JTable(reservationModel);
        JScrollPane reservationScrollPane = new JScrollPane(reservationTable);
        reservationPanel.add(reservationScrollPane, BorderLayout.CENTER);

        // 예약 관련 버튼
        JPanel reservationButtonPanel = new JPanel();
        JButton cancelReservationButton = new JButton("예약 취소");
        cancelReservationButton.addActionListener(e -> {
            int selectedRow = reservationTable.getSelectedRow();
            if (selectedRow >= 0) {
                String status = (String) reservationModel.getValueAt(selectedRow, 4);
                if ("완료".equals(status)) {
                    JOptionPane.showMessageDialog(reservationFrame, "완료된 예약은 취소할 수 없습니다.");
                    return;
                }
                int reservationId = (int) reservationModel.getValueAt(selectedRow, 0);
                reservationsDAO.cancelReservation(reservationId);
                reservationModel.setValueAt("예약중", selectedRow, 4);
                reservationModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(reservationFrame, "예약이 취소되었습니다.");
            } else {
                JOptionPane.showMessageDialog(reservationFrame, "취소할 예약을 선택하세요.");
            }
        });

        JButton completeReservationButton = new JButton("예약 완료");
        completeReservationButton.addActionListener(e -> {
            int selectedRow = reservationTable.getSelectedRow();
            if (selectedRow >= 0) {
                int reservationId = (int) reservationModel.getValueAt(selectedRow, 0);
                reservationsDAO.completeReservation(reservationId);
                reservationModel.setValueAt("완료", selectedRow, 4);
                JOptionPane.showMessageDialog(reservationFrame, "예약이 완료되었습니다.");
            } else {
                JOptionPane.showMessageDialog(reservationFrame, "완료할 예약을 선택하세요.");
            }
        });

        reservationButtonPanel.add(cancelReservationButton);
        reservationButtonPanel.add(completeReservationButton);
        reservationPanel.add(reservationButtonPanel, BorderLayout.SOUTH);

        List<RESERVATIONS> reservations = reservationsDAO.getAllReservations();
        for (RESERVATIONS reservation : reservations) {
            String bookName = rentalsDAO.getBookNameById(reservation.getBookID());
            Object[] row = { reservation.getRsID(), reservation.getUserID(), bookName, reservation.getRsDate(),
                    reservation.getRsState() };
            reservationModel.addRow(row);
        }

        tabbedPane.addTab("예약 관리", reservationPanel);

        // 대여 관리 탭
        JPanel rentalPanel = new JPanel();
        rentalPanel.setLayout(new BorderLayout());
        String[] rentalColumnNames = { "ID", "회원 ID", "도서명", "대여일", "상태" };
        DefaultTableModel rentalModel = new NonEditableTableModel(rentalColumnNames, 0);
        JTable rentalTable = new JTable(rentalModel);
        JScrollPane rentalScrollPane = new JScrollPane(rentalTable);
        rentalPanel.add(rentalScrollPane, BorderLayout.CENTER);

        // 대여 관련 버튼
        JPanel rentalButtonPanel = new JPanel();
        JButton registerRentalButton = new JButton("대여 등록");
        registerRentalButton.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(0, 2));
            JTextField userIdField = new JTextField();
            JTextField bookIdField = new JTextField();

            inputPanel.add(new JLabel("회원 ID:"));
            inputPanel.add(userIdField);
            inputPanel.add(new JLabel("도서 ID:"));
            inputPanel.add(bookIdField);

            int result = JOptionPane.showConfirmDialog(reservationFrame, inputPanel, "대여 등록",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String userID = userIdField.getText().trim();
                String bookIDString = bookIdField.getText().trim();

                if (userID.isEmpty() || bookIDString.isEmpty()) {
                    JOptionPane.showMessageDialog(reservationFrame, "모든 필드를 입력해야 합니다.");
                    return;
                }

                int bookID;
                try {
                    bookID = Integer.parseInt(bookIDString);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(reservationFrame, "유효한 도서 ID를 입력하세요.");
                    return;
                }

                if (!rentalsDAO.isUserExists(userID)) {
                    JOptionPane.showMessageDialog(reservationFrame, "존재하지 않는 회원 ID입니다.");
                    return;
                }

                String bookName = rentalsDAO.getBookNameById(bookID);
                if (bookName == null) {
                    JOptionPane.showMessageDialog(reservationFrame, "존재하지 않는 도서 ID입니다.");
                    return;
                }

                java.util.Date rentalDateUtil = new java.util.Date();
                java.sql.Date rentalDate = new java.sql.Date(rentalDateUtil.getTime()); // `toDate` 형식으로 변환

                java.util.Date returnDueDateUtil = new java.util.Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));
                java.sql.Date returnDueDate = new java.sql.Date(returnDueDateUtil.getTime()); // `toDate` 형식으로 변환

                RENTALS rental = new RENTALS();
                rental.setUserID(userID);
                rental.setBookID(bookID);
                rental.setRentalDate(rentalDate); // DB에 저장되는 날짜 형식
                rental.setReturnDueDate(returnDueDate); // DB에 저장되는 날짜 형식
                rental.setRentalState("대여");

                // Register rental and get the generated RentalID
                int rentalId = rentalsDAO.registerRentalAndGetId(rental); // 수정된 메서드 사용
                rental.setRentalId(rentalId); // ID 설정

                JOptionPane.showMessageDialog(reservationFrame, "대여 등록 완료되었습니다.");

                Object[] row = { rental.getRentalId(), rental.getUserID(), bookName, rental.getRentalDate(), rental.getRentalState() };
                rentalModel.addRow(row);
            }
        });


        JButton cancelRentalButton = new JButton("대여 취소");
        cancelRentalButton.addActionListener(e -> {
            int selectedRow = rentalTable.getSelectedRow();
            if (selectedRow >= 0) {
                String status = (String) rentalModel.getValueAt(selectedRow, 4);
                if ("완료".equals(status)) { // "완료" 상태인지 확인
                    JOptionPane.showMessageDialog(reservationFrame, "완료된 대여는 취소할 수 없습니다.");
                    return; // 작업 중단
                }
                int rentalId = (int) rentalModel.getValueAt(selectedRow, 0);
                rentalsDAO.cancelRental(rentalId);
                rentalModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(reservationFrame, "대여가 취소되었습니다.");
            } else {
                JOptionPane.showMessageDialog(reservationFrame, "취소할 대여를 선택하세요.");
            }
        });

        JButton returnCompleteButton = new JButton("반납 완료");
        returnCompleteButton.addActionListener(e -> {
            int selectedRow = rentalTable.getSelectedRow();
            if (selectedRow >= 0) {
                int rentalId = (int) rentalModel.getValueAt(selectedRow, 0);
                rentalsDAO.completeRental(rentalId);
                rentalModel.setValueAt("완료", selectedRow, 4); // 상태 업데이트
                JOptionPane.showMessageDialog(reservationFrame, "반납이 완료되었습니다.");
            } else {
                JOptionPane.showMessageDialog(reservationFrame, "반납할 대여를 선택하세요.");
            }
        });

        JButton lateRentalButton = new JButton("연체 등록");
        lateRentalButton.addActionListener(e -> {
            int selectedRow = rentalTable.getSelectedRow();
            if (selectedRow >= 0) {
            	String status = (String) rentalModel.getValueAt(selectedRow, 4);
                if ("완료".equals(status)) { // "완료" 상태인지 확인
                    JOptionPane.showMessageDialog(reservationFrame, "완료된 대여는 연체 등록할 수 없습니다.");
                    return; // 작업 중단
                }
                int rentalId = (int) rentalModel.getValueAt(selectedRow, 0);
                String userId = (String) rentalModel.getValueAt(selectedRow, 1);
                if(rentalsDAO.registerDelay(rentalId, userId) > 0) {
                	rentalModel.setValueAt("연체", selectedRow, 4); // 테이블에서 상태 업데이트
                    JOptionPane.showMessageDialog(reservationFrame, "연체가 등록되었습니다.");
                }
            } else {
                JOptionPane.showMessageDialog(reservationFrame, "연체할 대여를 선택하세요.");
            }
        });

        rentalButtonPanel.add(registerRentalButton);
        rentalButtonPanel.add(cancelRentalButton);
        rentalButtonPanel.add(returnCompleteButton);
        rentalButtonPanel.add(lateRentalButton);
        rentalPanel.add(rentalButtonPanel, BorderLayout.SOUTH);

        // 대여 정보 조회 및 테이블에 추가
        List<RENTALS> rentals = rentalsDAO.getAllRentals();
        for (RENTALS rental : rentals) {
            String bookName = rentalsDAO.getBookNameById(rental.getBookID());
            Object[] row = { rental.getRentalId(), rental.getUserID(), bookName, rental.getRentalDate(), rental.getRentalState() };
            rentalModel.addRow(row);
        }

        tabbedPane.addTab("대여 관리", rentalPanel);
        reservationFrame.add(tabbedPane);
        reservationFrame.setVisible(true);
    }

    private void showRecommendBooksWindow() {
        JFrame recommendBooksFrame = new JFrame("희망 도서 목록");
        recommendBooksFrame.setSize(800, 600);
        recommendBooksFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        recommendBooksFrame.setLayout(new BorderLayout());

        List<RECOMMENDBOOKS> recommendBooks = recommendBooksDAO.getAllRecommendBooks();

        // 테이블 모델 생성
        String[] columnNames = { "ID", "회원 ID", "도서 제목", "저자", "출판사", "출판일", "신청일", "상태" };
        DefaultTableModel model = new NonEditableTableModel(columnNames, 0); // NonEditableTableModel 사용

        // 데이터 추가
        for (RECOMMENDBOOKS book : recommendBooks) {
            Object[] row = { book.getRecommendID(), book.getUserID(), book.getBookName(), book.getWriter(),
                    book.getPublisher(), book.getPubDate(), book.getReDate(), book.getCompleteYN() };
            model.addRow(row);
        }

        // JTable 생성
        JTable table = new JTable(model); // NonEditableTableModel로 설정
        JScrollPane scrollPane = new JScrollPane(table);

        // 승인 및 반려 버튼
        JButton approveButton = new JButton("승인");
        approveButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int recommendBookId = (int) model.getValueAt(selectedRow, 0);
                recommendBooksDAO.approveRecommendBook(recommendBookId);
                JOptionPane.showMessageDialog(recommendBooksFrame, "추천 도서가 승인되었습니다.");

                // 상태를 'Y'로 변경
                model.setValueAt("Y", selectedRow, 7); // 7번째 열이 completeYN

                // 행 삭제
                model.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(recommendBooksFrame, "승인할 도서를 선택하세요.");
            }
        });

        JButton rejectButton = new JButton("반려");
        rejectButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int recommendBookId = (int) model.getValueAt(selectedRow, 0);
                recommendBooksDAO.rejectRecommendBook(recommendBookId);
                JOptionPane.showMessageDialog(recommendBooksFrame, "추천 도서가 반려되었습니다.");

                // 테이블에서 해당 행 삭제
                model.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(recommendBooksFrame, "반려할 도서를 선택하세요.");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);

        recommendBooksFrame.add(scrollPane, BorderLayout.CENTER);
        recommendBooksFrame.add(buttonPanel, BorderLayout.SOUTH);
        recommendBooksFrame.setVisible(true);
    }

	//카테고리 관리 정보를 조회하는 메서드
	private void showCategories() {
	    // 카테고리 정보를 보여주는 로직 작성
		new AdminCategoryUi();//카테고리 창 열기
	}
}
