package application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {
	
	Scene logInScene, profScene, stuScene, adminScene, signUpScene; // 각 페이지들
	static String id;
	Boolean stuIsCheck = false; // 학생 회원가입 ID 중복체크
	Boolean profIsCheck = false; // 교수 회원가입 ID 중복체크
	String prevId; // 회원가입 중복확인이 된 ID
	
	@Override
	public void start(Stage stage) {
		try {
			// 로그인 화면 구현
			Label lId = new Label("User ID : ");
			TextField tfId = new TextField();
			Label lPassword = new Label("Password : ");
			PasswordField tfPassword = new PasswordField();
			Label lMessage = new Label();

			RadioButton rbtnStu = new RadioButton("학 생");
			RadioButton rbtnProf = new RadioButton("교 수");
			rbtnStu.setPadding(new Insets(0, 0, 10, 0));
			rbtnProf.setPadding(new Insets(0, 0, 10, 30));

			ToggleGroup tg = new ToggleGroup();
			rbtnStu.setToggleGroup(tg);
			rbtnProf.setToggleGroup(tg);
			
			Button btnLogIn = new Button("로그인");
			Button btnFindId = new Button("ID 찾기");
			Button btnFindPassword = new Button("Password 찾기");
			Button btnSignUp = new Button("회원가입");
			
			// 회원가입 버튼 클릭 시
			btnSignUp.setOnAction(e -> {
				tfPassword.setText("");
				lMessage.setText("");
				
				stage.close(); // 현재 stage(로그인화면) 닫기
				sceneSignUp(stage); // 회원가입 stage로 전환
			});
			
			// 로그인 버튼 클릭 시
			btnLogIn.setOnAction(evevt -> {
				this.id = tfId.getText();
				lMessage.setStyle("-fx-text-fill: red;");
				String id = tfId.getText();
				String password = tfPassword.getText();
				
				try {
					// 신원(학생 or 교수)이 선택되어있지 않다면
					if(rbtnStu.isSelected() == false && rbtnProf.isSelected() == false) {
						try {
							AdministratorDAO administrator = new AdministratorDAO();
							String[] adminInfo = administrator.administratorLogInSelect(id);
							
							// 관리자 ID, Password가 일치한다면
							if(adminInfo[0].equals(id) && adminInfo[1].equals(password)) {
								tfPassword.setText("");
								lMessage.setText("");
								
								stage.close();
								sceneAdministrator(stage); // 관리자 계정 로그인
							}
						} catch (Exception e) {
							lMessage.setText("사용자의 신원을 선택해 주세요. (학생 or 교수)");
						}
					}else if(id.equals("")) {
						lMessage.setText("ID를 입력해 주세요.");
					} else if(password.equals("")) {
						lMessage.setText("Password를 입력해 주세요.");
					} else if(rbtnStu.isSelected()) { // 신원이 학생으로 선택되어 있다면
						StudentDAO student = new StudentDAO();
						String[] stuInfo = student.studentLogInSelect(id);
						if(stuInfo[0].equals(id) && stuInfo[1].equals(password)) { // 학생의 ID, Password가 일치한다면
							tfPassword.setText("");
							lMessage.setText("");
							rbtnStu.setSelected(false);
							
							stage.close();
							sceneStudent(stage); // 학생 계정 로그인
						}
					} else if(rbtnProf.isSelected()) { // 신원이 교수로 선택되어 있다면
						ProfessorDAO professor = new ProfessorDAO();
						String[] profInfo = professor.professorLogInSelect(id);
						if(profInfo[0].equals(id) && profInfo[1].equals(password)) { // 교수의 ID, Password가 일치한다면
							tfPassword.setText("");
							lMessage.setText("");
							rbtnProf.setSelected(false);
							
							stage.close();
							sceneProfessor(stage); // 교수 계정 로그인
						}
					}
				} catch(Exception e) {
					System.out.println("오류 : " + e.getMessage());
					lMessage.setText("ID 혹은 Password가 일치하지 않습니다.");
				}
			});

			GridPane identityGrid = new GridPane();
			identityGrid.addRow(0, rbtnStu, rbtnProf);
			identityGrid.setAlignment(Pos.CENTER);

			GridPane grid = new GridPane();
			grid.addRow(0, lId, tfId);
			grid.addRow(1, lPassword, tfPassword);
			grid.add(lMessage, 0, 2, 3, 1);
			grid.add(btnLogIn, 2, 0, 1, 2);
			btnLogIn.prefHeightProperty().bind(tfId.heightProperty().add(tfPassword.heightProperty()));
			grid.addRow(3, btnFindId, btnFindPassword, btnSignUp);

			GridPane mainGrid = new GridPane();
			mainGrid.addColumn(0, identityGrid, grid);
			mainGrid.setAlignment(Pos.CENTER);

			StackPane pane = new StackPane();
			pane.setStyle("-fx-font-family: 'Malgun Gothic';");
			pane.getChildren().add(mainGrid);
			logInScene = new Scene(pane, 400, 500);
			
			stage.setScene(logInScene);
			stage.setTitle("학사정보시스템");
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
//-------------------- 회원가입 화면 --------------------
	public void sceneSignUp(Stage stage) {
		// 회원가입 화면 구현
		RadioButton rbtnStuSignUp = new RadioButton("학 생"); 
		RadioButton rbtnProfSignUp = new RadioButton("교 수");
		ToggleGroup tg = new ToggleGroup();
		rbtnStuSignUp.setToggleGroup(tg);
		rbtnProfSignUp.setToggleGroup(tg);
		
		GridPane grid = new GridPane();
		grid.addRow(0, rbtnStuSignUp, rbtnProfSignUp);
		grid.setAlignment(Pos.TOP_CENTER);
		rbtnStuSignUp.setPadding(new Insets(80, 24, 10, 20));
		rbtnProfSignUp.setPadding(new Insets(80, 64, 10, 50));
		
		Label lId = new Label("  ＊I     D   : ");
		Label lPassword = new Label("＊Password : ");
		Label lStuId = new Label("＊학     번  : ");
		Label lName = new Label("＊이     름  : ");
		Label lDep = new Label("＊학     과  : ");
		Label lAddress = new Label("   주     소  : ");
		Label lPhoneNumber = new Label(" 전 화 번 호 : ");
		
		TextField tfId = new TextField();
		PasswordField tfPassword = new PasswordField();
		TextField tfStuId = new TextField();
		tfStuId.setPromptText("숫자만 입력 가능");
		TextField tfName = new TextField();
		
		DepartmentDAO department = new DepartmentDAO();
		List<String> departments = department.departmentSignUpSelect(); // 모든 학과를 가져와 List에 넣는다.
		ObservableList<String> lstDepartment = FXCollections.observableArrayList(departments); // ObserVableList에 학과 list를 적용한다.
        ComboBox<String> cbDepartment = new ComboBox<String>(lstDepartment); // 저장한 ObservableList값을 학과선택comboBox에 출력
		
		TextField tfAddress = new TextField();
		TextField tfPhoneNumber = new TextField();
		tfPhoneNumber.setPromptText("'-'없이 입력");
		Button btnStuIdCheck = new Button("중복 확인");
		Button btnProfIdCheck = new Button("중복 확인");
		Button btnSignUp = new Button("회원가입");
		btnSignUp.setPrefSize(80, 30);
		
		Alert alert = new Alert(AlertType.NONE,"" , ButtonType.OK);
		alert.setTitle("회원가입");
		
		btnStuIdCheck.setOnAction(e -> { // 학생 회원가입 ID중복 체크버튼 클릭 시
			StudentDAO student = new StudentDAO();
			int count = student.studentIdCheckSelect(tfId.getText()); // 입력한 ID와 DB에서 가져온 ID를 비교하여 중복되는 데이터 수 저장
			Predicate<Integer> checked = (i) -> i == 0; // i가 0이면(중복되는 수가 0이면) true 반환
			stuIsCheck = checked.test(count); // i에 count를 입력하여 반환값을 bool형으로 저장
			
			if(stuIsCheck) { // stuIsCheck가 true일 시 즉, count가 0일 시 즉, 중복된 데이터가 없을 시
				alert.setContentText("사용 가능한 ID 입니다.");
				alert.showAndWait();
			} else if(count == -1) { // 입력한 ID가 없을 시 -1 반환 (studentIdCheckSelect 메소드 참조)
				alert.setContentText("ID를 입력해 주세요.");
				alert.showAndWait();
			} else { // stuIsCheck가 false일 시 즉, count가 1 이상일 시 즉, 중복된 데이터가 있을 시
				alert.setContentText("중복된 ID 입니다.\n다른 ID를 입력해 주세요.");
				alert.showAndWait();
			}
			prevId = tfId.getText(); // 중복체크하는 순간 현재 입력한 ID값 저장
		});
		
		btnProfIdCheck.setOnAction(e -> { // 교수 회원가입 ID중복 체크버튼 클릭 시
			ProfessorDAO professor = new ProfessorDAO();
			int count = professor.professorIdCheckSelect(tfId.getText()); // 입력한 ID와 DB에서 가져온 ID를 비교하여 중복되는 데이터 수 저장
			Predicate<Integer> checked = (i) -> i == 0; // i가 0이면 true 반환
			profIsCheck = checked.test(count); // i에 count를 입력하여 반환값을 bool형으로 저장
			
			if(profIsCheck) { // profIsCheck가 true일 시 즉, count가 0일 시 즉, 중복된 데이터가 없을 시
				alert.setContentText("사용 가능한 ID 입니다.");
				alert.showAndWait();
			} else if(count == -1) { // 입력한 ID가 없을 시 -1 반환 (professorIdCheckSelect 메소드 참조)
				alert.setContentText("ID를 입력해 주세요.");
				alert.showAndWait();
			} else { // profIsCheck가 false일 시 즉, count가 1 이상일 시 즉, 중복된 데이터가 있을 시
				alert.setContentText("중복된 ID 입니다.\n다른 ID를 입력해 주세요.");
				alert.showAndWait();
			}
			prevId = tfId.getText(); // 중복체크하는 순간 현재 입력한 ID값 저장
		});
		
		 // toggleGroup(rbtnStuSignUp, rbtnProfSignUp)이 변하는지 확인
		tg.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			// 변할 시 실행되는 메소드
            public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n){
            	RadioButton rb = (RadioButton)tg.getSelectedToggle(); // 선택된 라디오버튼 값 저장
                String rbTxt = rb.getText(); // 선택된 라디오버튼 text값 저장
                Text txt = new Text("");
                tfId.clear(); tfPassword.clear(); tfStuId.clear(); tfName.clear(); tfAddress.clear(); tfPhoneNumber.clear(); // 각 textField 초기화

                if(rbTxt.equals(rbtnStuSignUp.getText())) { // 학생 버튼 클릭 시
                	stuIsCheck = false; // 중복확인 초기화
                	prevId = ""; // 중복확인 시 저장된 ID값 초기화
                	
                	// 학생 회원가입 화면에 필요한 node 추가
                	grid.getChildren().clear();
            		grid.addRow(0, rbtnStuSignUp, rbtnProfSignUp);
                	grid.addColumn(0, lId, txt, lPassword, lStuId, lName, lDep, lAddress, lPhoneNumber);
                	grid.addColumn(1, tfId, btnStuIdCheck, tfPassword, tfStuId, tfName, cbDepartment, tfAddress, tfPhoneNumber);
                	grid.add(btnSignUp, 1, 9);
                	
                } else if(rbTxt.equals(rbtnProfSignUp.getText())) { // 교수 버튼 클릭 시
                	profIsCheck = false; // 중복확인 초기화
                	prevId = ""; // 중복확인 시 저장된 ID값 초기화
                	
                	// 교수 회원가입 화면에 필요한 node 추가
                	grid.getChildren().clear();
                	grid.addRow(0, rbtnStuSignUp, rbtnProfSignUp);
                	grid.addColumn(0, lId, txt, lPassword, lName, lDep, lAddress, lPhoneNumber);
                	grid.addColumn(1, tfId, btnProfIdCheck, tfPassword, tfName, cbDepartment, tfAddress, tfPhoneNumber);
                	grid.add(btnSignUp, 1, 9);
                }
                
                btnSignUp.setOnAction(e -> { // 회원가입 버튼 클릭 시
        			DepartmentDAO department = new DepartmentDAO();
        			// 선택한 학과에 맞는 학과 번호를 DB에서 찾아와 저장
        			int dep_no = department.departmentNoSelect(cbDepartment.getValue());
        			
        			if(tfId.getText().equals("")) {
        				alert.setContentText("ID를 입력해 주세요.");
        				alert.showAndWait();
        			} else if(tfPassword.getText().equals("")) {
        				alert.setContentText("Password를 입력해 주세요.");
        				alert.showAndWait();
        			}else if(tfName.getText().equals("")) {
        				alert.setContentText("이름을 입력해 주세요.");
        				alert.showAndWait();
        			}else if(cbDepartment.getValue() == null) {
        				alert.setContentText("학과를 입력해 주세요.");
        				alert.showAndWait();
        				
        			// 회원가입 신원이 교수(교수 버튼 클릭) AND 중복확인 완료 AND 중복확인 완료된 ID값이 맞다면
        			} else if(rbtnProfSignUp.isSelected() && profIsCheck && prevId.equals(tfId.getText())) {
        				ProfessorDAO professor = new ProfessorDAO();
        				
        				// 교수 정보 DB에 회원가입 정보 저장
        				professor.professorSignUpInsert(tfId.getText(), tfPassword.getText(), tfName.getText(), dep_no, tfAddress.getText(), tfPhoneNumber.getText());
        				alert.setTitle("회원가입 성공!");
        				alert.setContentText("회원가입이 완료되었습니다.\n로그인 화면으로 이동합니다.");
        				alert.showAndWait();
        				
        				stage.close();
        				stage.setScene(logInScene); // 로그인 화면으로 전환
        				stage.show();
        			} else if(rbtnStuSignUp.isSelected() && tfStuId.getText().equals("")) {
                    	if (rbTxt.equals(rbtnStuSignUp.getText())) {
                    		alert.setContentText("학번을 입력해 주세요.");
                    		alert.showAndWait();
                    	}
                    
                    // 회원가입 신원이 학생(학생 버튼 클릭) AND 중복확인 완료 AND 중복확인 완료된 ID값이 맞다면
        			} else if(rbtnStuSignUp.isSelected() && stuIsCheck && prevId.equals(tfId.getText())) {
        				StudentDAO student = new StudentDAO();
        				// 학번 데이터타입을 int형으로 변환
        				int student_id = Integer.parseInt(tfStuId.getText());
        				// 학생 정보 DB에 회원가입 정보 저장
        				student.studentSignUpInsert(student_id, tfId.getText(), tfPassword.getText(), tfName.getText(), dep_no, tfAddress.getText(), tfPhoneNumber.getText());
        				alert.setTitle("회원가입 성공!");
        				alert.setContentText("회원가입이 완료되었습니다.\n로그인 화면으로 이동합니다.");
        				alert.showAndWait();
        				
        				stage.close();
        				stage.setScene(logInScene); // 로그인 화면으로 전환
        				stage.show();
        			} else {
        				alert.setContentText("ID 중복확인을 해 주세요.");
                		alert.showAndWait();
        			}
                });
            }
		});

		grid.setPadding(new Insets(0, 0, 0, 12));
		GridPane.setMargin(btnSignUp, new Insets(10, 0, 0, 85));
		grid.setVgap(10);
		grid.setStyle("-fx-font-family: 'Malgun Gothic';");
		signUpScene = new Scene(grid, 400, 500);
		stage.setScene(signUpScene);
		stage.show();
	}
	
//-------------------- 학생 메인 화면 --------------------
	public void sceneStudent(Stage stage) {
		BorderPane pane = new BorderPane();
		Label title = new Label("학사관리시스템");
		title.setStyle("-fx-font-size:20; -fx-font-weight:bold");
		title.setPrefHeight(50);
		BorderPane.setAlignment(title, Pos.CENTER);
		pane.setTop(title);
		
		VBox vBox = new VBox();
		Button btnStuManage = new Button("수강과목");
		Button btnProfManage = new Button("수강신청");
		Button btnDepManage = new Button("자료실");
		Button btnBoardManage = new Button("학교일정");
		Button btnInfoManage = new Button("내 정보");
		
		btnStuManage.setPrefSize(100, 40);
		btnStuManage.setStyle("-fx-font-size:14");
		btnProfManage.setPrefSize(100, 40);
		btnProfManage.setStyle("-fx-font-size:14");
		btnDepManage.setPrefSize(100, 40);
		btnDepManage.setStyle("-fx-font-size:14");
		btnBoardManage.setPrefSize(100, 40);
		btnBoardManage.setStyle("-fx-font-size:14");
		btnInfoManage.setPrefSize(100, 40);
		btnInfoManage.setStyle("-fx-font-size:14");
		
		VBox.setMargin(btnStuManage, new Insets(60, 0, 0, 0));
		vBox.getChildren().addAll(btnStuManage, btnProfManage, btnDepManage, btnBoardManage, btnInfoManage);
		pane.setLeft(vBox);
		
		Label lblTime = new Label();
		Boolean stop = false;
		Thread thread = new Thread() {
			@Override
			public void run() {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd E HH:mm", Locale.KOREA);
				while(!stop) {
					String strTime = sdf.format(new Date());
					Platform.runLater(() -> {
						lblTime.setText(strTime);
					});
					try {Thread.sleep(100);} catch (InterruptedException e) {}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		
		BorderPane.setAlignment(lblTime, Pos.BOTTOM_RIGHT);
      BorderPane.setMargin(lblTime, new Insets(5, 5, 0, 0));
      pane.setBottom(lblTime);
		
		pane.setStyle("-fx-font-family: 'Malgun Gothic';");
		stuScene = new Scene(pane, 900, 500);
		stage.setScene(stuScene);
		stage.show();
	}

//-------------------- 교수 메인 화면 --------------------
	public void sceneProfessor(Stage stage) {
		Label label = new Label("교수 페이지 입니다.");
		Button btnLogOut = new Button("로그아웃");
		
		btnLogOut.setOnAction(e -> {
			stage.close();
			stage.setScene(logInScene);
			stage.show();
		});
		
		GridPane grid = new GridPane();
		grid.addRow(0, btnLogOut, label);
		grid.setAlignment(Pos.TOP_RIGHT);
 
		StackPane pane = new StackPane();
		pane.getChildren().add(grid);
		pane.setStyle("-fx-font-family: 'Malgun Gothic';");
		
		profScene = new Scene(pane, 400, 500);
		stage.setScene(profScene);
		stage.show();
	}

//-------------------- 관리자 메인 화면 --------------------
	public void sceneAdministrator(Stage stage) {
		BorderPane pane = new BorderPane();
		
		Label title = new Label("관 리 자");
		title.setStyle("-fx-font-size:20; -fx-font-weight:bold");
		title.setPrefHeight(50);
		BorderPane.setAlignment(title, Pos.CENTER);
		pane.setTop(title);
		
		VBox vBox = new VBox();
		Button btnStuManage = new Button("학   생");
		Button btnProfManage = new Button("교   수");
		Button btnDepManage = new Button("학   과");
		Button btnBoardManage = new Button("게 시 판");
		Button btnInfoManage = new Button("내 정보");
		
		btnStuManage.setPrefSize(100, 40);
		btnStuManage.setStyle("-fx-font-size:14");
		btnProfManage.setPrefSize(100, 40);
		btnProfManage.setStyle("-fx-font-size:14");
		btnDepManage.setPrefSize(100, 40);
		btnDepManage.setStyle("-fx-font-size:14");
		btnBoardManage.setPrefSize(100, 40);
		btnBoardManage.setStyle("-fx-font-size:14");
		btnInfoManage.setPrefSize(100, 40);
		btnInfoManage.setStyle("-fx-font-size:14");
		
		VBox.setMargin(btnStuManage, new Insets(60, 0, 0, 0));
		vBox.getChildren().addAll(btnStuManage, btnProfManage, btnDepManage, btnBoardManage, btnInfoManage);
		pane.setLeft(vBox);
		
		// 학생 버튼 클릭 시 (학생 관리)
		btnStuManage.setOnAction(e -> {
			title.setText("학생관리");
			
			Label lDep = new Label("학 과 : ");
			Label lStuId = new Label("학 번 : ");
			Label lName = new Label("이 름 : ");
			
			DepartmentDAO department = new DepartmentDAO();
			List<String> departments = department.departmentSignUpSelect();
			ObservableList<String> lstDepartment = FXCollections.observableArrayList(departments);
	        ComboBox<String> cbDep = new ComboBox<String>(lstDepartment); // 저장한 list값을 학과선택comboBox에 출력
	        cbDep.setEditable(true);
	        cbDep.setPrefSize(130, 20);
	        
	        TextField tfStuId = new TextField();
	        tfStuId.setPromptText("숫자만 입력");
	        tfStuId.setPrefSize(130, 20);
	        TextField tfName = new TextField();
	        tfName.setPrefSize(130, 20);
	        Button btnSelect = new Button("검 색");
	        btnSelect.setPrefSize(80, 20);
	        HBox hBox = new HBox();
	        hBox.getChildren().addAll(lDep, cbDep, lStuId, tfStuId, lName, tfName, btnSelect);
	        HBox.setMargin(lDep, new Insets(3, 0, 0, 20));
	        HBox.setMargin(lStuId, new Insets(3, 0, 0, 20));
	        HBox.setMargin(lName, new Insets(3, 0, 0, 20));
	        HBox.setMargin(btnSelect, new Insets(0, 20, 0, 67));
	        
	        TableColumn<Student, Integer> stuIdColumn = new TableColumn<>("학 번");
	        stuIdColumn.setPrefWidth(90);
	        stuIdColumn.setCellValueFactory(new PropertyValueFactory<>("student_id"));
	        
	        TableColumn<Student, String> nameColumn = new TableColumn<>("이 름");
	        nameColumn.setPrefWidth(70);
	        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
	        
	        TableColumn<Student, String> depColumn = new TableColumn<>("학 과");
	        depColumn.setPrefWidth(100);
	        depColumn.setCellValueFactory(new PropertyValueFactory<>("department_name"));
	        
	        TableColumn<Student, String> idColumn = new TableColumn<>("I D");
	        idColumn.setPrefWidth(90);
	        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
	        
	        TableColumn<Student, String> passwordColumn = new TableColumn<>("Password");
	        passwordColumn.setPrefWidth(90);
	        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
	        
	        TableColumn<Student, String> addressColumn = new TableColumn<>("주 소");
	        addressColumn.setPrefWidth(200);
	        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
	        
	        TableColumn<Student, String> phoneNumberColumn = new TableColumn<>("연 락 처");
	        phoneNumberColumn.setPrefWidth(120);
	        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
	        
	        TableView<Student> tableView = new TableView<>();
	        tableView.setPlaceholder(new Label("정보를 입력해 주세요."));
	        tableView.getColumns().addAll(stuIdColumn, nameColumn, depColumn, idColumn, passwordColumn, addressColumn, phoneNumberColumn);
	        btnSelect.setOnAction(e2 -> {
	        	int stuId;
	        	try {
	        		stuId = Integer.parseInt(tfStuId.getText());
	        	} catch(NumberFormatException e3) {
	    			stuId = 0;
	        	}
	        	AdministratorDAO admin = new AdministratorDAO();
	        	ObservableList<Student> tableItems = admin.studentInfoSelect(cbDep.getValue(), stuId, tfName.getText());
	        	if(tableItems.size() > 0) {
	        		tableView.setItems(tableItems);
	        	} else {
	        		Alert alert = new Alert(AlertType.WARNING);
	        		alert.setTitle("관리자 - 학생 관리");
	        		alert.setHeaderText("학생 계정 검색 실패");
	        		alert.setContentText("학생 정보가 일치하지 않습니다.");
	        		alert.showAndWait();
	        		tableView.getItems().clear();
	        	}
	        });
	        
	        Button btnInsert = new Button("추 가");
	        Button btnUpdate = new Button("수 정");
	        Button btnDelete = new Button("삭 제");
	        GridPane btnGrid = new GridPane();
	        btnInsert.setPrefSize(100, 50);
	        btnUpdate.setPrefSize(100, 50);
	        btnDelete.setPrefSize(100, 50);
	        btnGrid.addRow(0, btnInsert, btnUpdate, btnDelete);
	        btnGrid.setAlignment(Pos.CENTER_RIGHT);
	        GridPane.setMargin(btnInsert, new Insets(0, 20, 0, 0));
	        GridPane.setMargin(btnUpdate, new Insets(0, 20, 0, 0));
	        GridPane.setMargin(btnDelete, new Insets(0, 10, 0, 0));
	        
	        btnInsert.setOnAction(e2 -> {
	        	AdminWorkStage.adminInsertStuInfo();
	        });
	        
	        btnUpdate.setOnAction(e2 -> {
	        	ObservableList<Student> stuSelected = tableView.getSelectionModel().getSelectedItems();
	        	if(stuSelected.size() != 0) {
		        	AdminWorkStage.adminUpdateStuInfo(stuSelected.get(0).getStudent_id(), stuSelected.get(0).getName(), stuSelected.get(0).getDepartment_name(),
		        			stuSelected.get(0).getId(), stuSelected.get(0).getPassword(), stuSelected.get(0).getAddress(), stuSelected.get(0).getPhoneNumber());
	        	} else {}
	        });
	        
	        btnDelete.setOnAction(e2 -> {
	        	
	        	ObservableList<Student> stuSelected = tableView.getSelectionModel().getSelectedItems();
	        	Alert alert = new Alert(AlertType.NONE,"" , ButtonType.OK);
	        	alert.setTitle("관리자 - 학생 관리");
	        	if (stuSelected.size() != 0) {
			    	alert.setHeaderText("학생 계정 삭제");
			    	alert.setContentText("학번 : " + stuSelected.get(0).getStudent_id() + "\n"
			    							+ "이름 : " + stuSelected.get(0).getName() + "\n"
			    							+ "학과 : " + stuSelected.get(0).getDepartment_name() + "\n"
			    							+ "학생 계정을 삭제하시겠습니까?");
			    	ButtonType btnOk = new ButtonType("Yes", ButtonBar.ButtonData.YES);
			    	ButtonType btnCancel = new ButtonType("No", ButtonBar.ButtonData.NO);
			    	alert.getButtonTypes().setAll(btnOk, btnCancel);
			    	alert.showAndWait().ifPresent(type -> {
			    		if(type.equals(btnOk)) {
			    			AdministratorDAO admin = new AdministratorDAO();
			    			admin.studentInfoDelete(stuSelected.get(0).getStudent_id());
			    			Alert alert2 = new Alert(AlertType.NONE,"" , ButtonType.OK);
			    			alert2.setTitle("관리자 - 학생 관리");
			    			alert2.setHeaderText("삭제 완료");
			    			alert2.setContentText("학생 계정이 정상적으로 삭제되었습니다.");
			    			alert2.showAndWait();
			    		} else {}
			    	});
	        	} else {
	        		alert.setContentText("학생 정보를 선택해 주세요.");
	        		alert.showAndWait();
	        	}
	        });
	        
	        GridPane grid = new GridPane();
	        grid.addRow(0, hBox);
	        grid.addRow(1, tableView);
	        grid.addRow(2, btnGrid);
	        grid.setPadding(new Insets(0, 20, 0, 20));
	        grid.setVgap(10);
	        pane.setCenter(grid);
		});
		
		
		
		// 교수 버튼 클릭 시 (교수 관리)
		btnProfManage.setOnAction(e -> {
			title.setText("교수관리");
			
			Label lDep = new Label("학 과 : ");
			Label lName = new Label("이 름 : ");
			
			DepartmentDAO department = new DepartmentDAO();
			List<String> departments = department.departmentSignUpSelect();
			ObservableList<String> lstDepartment = FXCollections.observableArrayList(departments);
	        ComboBox<String> cbDep = new ComboBox<String>(lstDepartment); // 저장한 list값을 학과선택comboBox에 출력
	        cbDep.setEditable(true);
	        cbDep.setPrefSize(130, 20);
	        
	        TextField tfName = new TextField();
	        tfName.setPrefSize(130, 20);
	        Button btnSelect = new Button("검 색");
	        btnSelect.setPrefSize(80, 20);
	        HBox hBox = new HBox();
	        hBox.getChildren().addAll(lDep, cbDep, lName, tfName, btnSelect);
	        HBox.setMargin(lDep, new Insets(3, 0, 0, 20));
	        HBox.setMargin(lName, new Insets(3, 0, 0, 20));
	        HBox.setMargin(btnSelect, new Insets(0, 20, 0, 67));
	        
	        TableColumn<Professor, String> nameColumn = new TableColumn<>("이 름");
	        nameColumn.setPrefWidth(85);
	        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
	        
	        TableColumn<Professor, String> depColumn = new TableColumn<>("학 과");
	        depColumn.setPrefWidth(115);
	        depColumn.setCellValueFactory(new PropertyValueFactory<>("department_name"));
	        
	        TableColumn<Professor, String> idColumn = new TableColumn<>("I D");
	        idColumn.setPrefWidth(105);
	        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
	        
	        TableColumn<Professor, String> passwordColumn = new TableColumn<>("Password");
	        passwordColumn.setPrefWidth(105);
	        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
	        
	        TableColumn<Professor, String> addressColumn = new TableColumn<>("주 소");
	        addressColumn.setPrefWidth(215);
	        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
	        
	        TableColumn<Professor, String> phoneNumberColumn = new TableColumn<>("연 락 처");
	        phoneNumberColumn.setPrefWidth(135);
	        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
	        
	        TableView<Professor> tableView = new TableView<>();
	        tableView.setPlaceholder(new Label("정보를 입력해 주세요."));
	        tableView.getColumns().addAll(nameColumn, depColumn, idColumn, passwordColumn, addressColumn, phoneNumberColumn);
	        btnSelect.setOnAction(e2 -> {
	        	AdministratorDAO admin = new AdministratorDAO(); 
	        	ObservableList<Professor> tableItems = admin.professorInfoSelect(cbDep.getValue(), tfName.getText());
	        	if(tableItems.size() > 0) {
	        		tableView.setItems(tableItems);
	        	} else {
	        		Alert alert = new Alert(AlertType.WARNING);
	        		alert.setTitle("관리자 - 교수 관리");
	        		alert.setHeaderText("교수 계정 검색 실패");
	        		alert.setContentText("교수 정보가 일치하지 않습니다.");
	        		alert.showAndWait();
	        		tableView.getItems().clear();
	        	}
	        });
	        
	        Button btnInsert = new Button("추 가");
	        Button btnUpdate = new Button("수 정");
	        Button btnDelete = new Button("삭 제");
	        GridPane btnGrid = new GridPane();
	        btnInsert.setPrefSize(100, 50);
	        btnUpdate.setPrefSize(100, 50);
	        btnDelete.setPrefSize(100, 50);
	        btnGrid.addRow(0, btnInsert, btnUpdate, btnDelete);
	        btnGrid.setAlignment(Pos.CENTER_RIGHT);
	        GridPane.setMargin(btnInsert, new Insets(0, 20, 0, 0));
	        GridPane.setMargin(btnUpdate, new Insets(0, 20, 0, 0));
	        GridPane.setMargin(btnDelete, new Insets(0, 10, 0, 0));
	        
	        btnInsert.setOnAction(e2 -> {
	        	AdminWorkStage.adminInsertProfInfo();
	        });
	        
	        btnUpdate.setOnAction(e2 -> {
	        	ObservableList<Professor> profSelected = tableView.getSelectionModel().getSelectedItems();
	        	if(profSelected.size() != 0) {
		        	AdminWorkStage.adminUpdateProfInfo(profSelected.get(0).getName(), profSelected.get(0).getDepartment_name(),
		        			profSelected.get(0).getId(), profSelected.get(0).getPassword(), profSelected.get(0).getAddress(), profSelected.get(0).getPhoneNumber());
	        	} else {}
	        });
	        
	        btnDelete.setOnAction(e2 -> {
	        	
	        	ObservableList<Professor> profSelected = tableView.getSelectionModel().getSelectedItems();
	        	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	        	alert.setTitle("관리자 - 교수 관리");
	        	if (profSelected.size() != 0) {
			    	alert.setHeaderText("교수 계정 삭제");
			    	alert.setContentText("이름 : " + profSelected.get(0).getName() + "\n"
			    							+ "학과 : " + profSelected.get(0).getDepartment_name() + "\n"
			    							+ "교수 계정을 삭제하시겠습니까?");
			    	ButtonType btnOk = new ButtonType("Yes", ButtonBar.ButtonData.YES);
			    	ButtonType btnCancel = new ButtonType("No", ButtonBar.ButtonData.NO);
			    	alert.getButtonTypes().setAll(btnOk, btnCancel);
			    	alert.showAndWait().ifPresent(type -> {
			    		if(type.equals(btnOk)) {
			    			AdministratorDAO admin = new AdministratorDAO();
			    			admin.professorInfoDelete(profSelected.get(0).getId());
			    			Alert alert2 = new Alert(AlertType.NONE,"" , ButtonType.OK);
							alert2.setTitle("관리자 - 교수 관리");
							alert2.setHeaderText("삭제 완료");
			    			alert2.setContentText("교수 계정이 정상적으로 삭제되었습니다.");
			    			alert2.showAndWait();
			    		} else {}
			    	});
	        	} else {
	        		alert.setContentText("교수 정보를 선택해 주세요.");
	        		alert.showAndWait();
	        	}
	        });
	        
	        GridPane grid = new GridPane();
	        grid.addRow(0, hBox);
	        grid.addRow(1, tableView);
	        grid.addRow(2, btnGrid);
	        grid.setPadding(new Insets(0, 20, 0, 20));
	        grid.setVgap(10);
	        pane.setCenter(grid);
		});
		
		// 학과 버튼 클릭 시(학과 관리)
		btnDepManage.setOnAction(e -> {
			title.setText("학과관리");
			
			TableColumn<Department, Integer> noColumn = new TableColumn<>("No");
			noColumn.setPrefWidth(50);
			noColumn.setCellValueFactory(new PropertyValueFactory<>("no"));
			
	        TableColumn<Department, Integer> nameColumn = new TableColumn<>("학과명");
	        nameColumn.setPrefWidth(175);
	        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
	        
			TableView<Department> tableView = new TableView<>();
			tableView.getColumns().addAll(noColumn, nameColumn);
			AdministratorDAO admin = new AdministratorDAO();
			tableView.setItems(admin.departmentInfoSelect());
			
			Label lDetails = new Label("학과 정보");
			lDetails.setStyle("-fx-font-size:18");
			lDetails.setLayoutX(212); lDetails.setLayoutY(30);
			
			Label lNo = new Label("No");
			lNo.setStyle("-fx-font-size:16");
			Label lValNo = new Label();
			lValNo.setStyle("-fx-font-size:16");
			Label lName = new Label("학과명");
			lName.setStyle("-fx-font-size:16");
			Label lValName = new Label();
			lValName.setStyle("-fx-font-size:16");
			Label lCntStu = new Label("학생 인원수");
			lCntStu.setStyle("-fx-font-size:16");
			Label lValCntStu = new Label();
			lValCntStu.setStyle("-fx-font-size:16");
			Label lCntProf = new Label("교수 인원수");
			lCntProf.setStyle("-fx-font-size:16");
			Label lValCntProf = new Label();
			lValCntProf.setStyle("-fx-font-size:16");
			Label lCntTotal = new Label("총 원");
			lCntTotal.setStyle("-fx-font-size:16");
			Label lValCntTotal = new Label();
			lValCntTotal.setStyle("-fx-font-size:16");
			
			tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
				ObservableList<Department> depSelected = tableView.getSelectionModel().getSelectedItems();
			    if (newSelection != null) {
			    	String depNo = Integer.toString(depSelected.get(0).getNo());
					lValNo.setText(depNo);
					
					lValName.setText(depSelected.get(0).getName());
					
					int[] count = admin.departmentCntSelect(depSelected.get(0).getNo());
					String cntStudent = Integer.toString(count[0]);
					lValCntStu.setText(cntStudent + " 명");
					
					String cntProfessor = Integer.toString(count[1]);
					lValCntProf.setText(cntProfessor + " 명");
					
					int Total = count[0] + count[1];
					String cntTotal = Integer.toString(Total);
					lValCntTotal.setText(cntTotal + " 명");
			    }
			});

			Button btnInsert = new Button("추 가");
			Button btnUpdate = new Button("수 정");
			Button btnDelete = new Button("삭 제");
			
			TextInputDialog input = new TextInputDialog();
			input.setTitle("관리자 - 학과 관리");
			
			btnInsert.setOnAction(e2 -> {
				input.setHeaderText("학과 추가");
				input.setContentText("추가하려는 학과명을 입력하세요 : ");
				Optional<String> result = input.showAndWait();
				
				result.ifPresent(name -> {
					admin.departmentInsert(name);
				});
				
				tableView.setItems(admin.departmentInfoSelect());
			});
			
			btnUpdate.setOnAction(e2 -> {
				ObservableList<Department> depSelected = tableView.getSelectionModel().getSelectedItems();
				if(depSelected.size() != 0) {
					input.setHeaderText("학과번호 : " + depSelected.get(0).getNo() + "\n학과명 : " + depSelected.get(0).getName());
					input.setContentText("변경하려는 학과명을 입력하세요 : ");
					Optional<String> result = input.showAndWait();
					
					result.ifPresent(name ->{
						admin.departmentNameUpdate(depSelected.get(0).getNo(), name);
					});
					
					tableView.setItems(admin.departmentInfoSelect());
				} else {
					Alert alert = new Alert(AlertType.NONE,"" , ButtonType.OK);
					alert.setTitle("관리자 - 학과 관리");
					alert.setContentText("수정하려는 학과를 선택해 주세요.");
					alert.showAndWait();
				}
			});

			btnDelete.setOnAction(e2 -> {
				ObservableList<Department> depSelected = tableView.getSelectionModel().getSelectedItems();
				if(depSelected.size() != 0) {
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		        	alert.setTitle("관리자 - 학과 관리");
		        	alert.setHeaderText("학과 삭제");
		        	alert.setContentText("학과번호 : " + depSelected.get(0).getNo() + "\n학과명 : " + depSelected.get(0).getName() + "\n\n"
		        			+ "학과를 삭제하시겠습니까?\n(삭제된 학과는 복구가 불가능합니다.)");
		        	
			    	ButtonType btnOk = new ButtonType("Yes", ButtonBar.ButtonData.YES);
			    	ButtonType btnCancel = new ButtonType("No", ButtonBar.ButtonData.NO);
			    	alert.getButtonTypes().setAll(btnOk, btnCancel);
			    	alert.showAndWait().ifPresent(type -> {
			    		if(type.equals(btnOk)) {
			    			admin.departmentDelete(depSelected.get(0).getNo());
			    		}
			    	});
					
					tableView.setItems(admin.departmentInfoSelect());
				} else {
					Alert alert2 = new Alert(AlertType.NONE,"" , ButtonType.OK);
					alert2.setTitle("관리자 - 학과 관리");
					alert2.setContentText("삭제하려는 학과를 선택해 주세요.");
					alert2.showAndWait();
				}
			});
			
			GridPane grid = new GridPane();
			grid.setVgap(25);
			grid.setHgap(100);
			grid.addColumn(0, lNo, lName, lCntStu, lCntProf, lCntTotal);
			grid.addColumn(1, lValNo, lValName, lValCntStu, lValCntProf, lValCntTotal);
			grid.setLayoutX(145); grid.setLayoutY(90);
			
			ButtonBar buttonBar = new ButtonBar();
			buttonBar.getButtons().addAll(btnInsert, btnUpdate, btnDelete);
			buttonBar.setLayoutX(250); buttonBar.setLayoutY(355);
			
			AnchorPane anchor = new AnchorPane();
			anchor.getChildren().addAll(lDetails, grid, buttonBar);
			
			SplitPane split = new SplitPane();
			BorderPane.setMargin(split, new Insets(20, 20, 20, 20));
			split.setDividerPositions(0.3);
			split.getItems().addAll(tableView, anchor);
			pane.setCenter(split);
			
		});
		
		// 게시판 버튼 클릭 시(게시판 관리)
		btnBoardManage.setOnAction(e -> {
		
			// 아직 게시판을 만들지 않아 나중에 구현@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
			
		});
		
		// 내 정보 버튼 클릭 시(내 정보 관리)
		btnInfoManage.setOnAction(e -> {
			title.setText("내 정보 관리");
			BorderPane.setMargin(title, new Insets(20, 0, 0, 0));
			
			Label lNo = new Label("No");
			lNo.setStyle("-fx-font-size:18");
			Label lName = new Label("이 름");
			lName.setStyle("-fx-font-size:18");
			Label lId = new Label("I D");
			lId.setStyle("-fx-font-size:18");
			Label lPassword = new Label("Password");
			lPassword.setStyle("-fx-font-size:18");
			Label lValNo = new Label();
			lValNo.setStyle("-fx-font-size:18");
			Label lValName = new Label();
			lValName.setStyle("-fx-font-size:18");
			Label lValId = new Label();
			lValId.setStyle("-fx-font-size:18");
			Label lValPassword = new Label();
			lValPassword.setStyle("-fx-font-size:18");
			Label label1 = new Label("｜");
			Label label2 = new Label("｜");
			Label label3 = new Label("｜");
			Label label4 = new Label("｜");
			label1.setStyle("-fx-font-size:20");
			label2.setStyle("-fx-font-size:20");
			label3.setStyle("-fx-font-size:20");
			label4.setStyle("-fx-font-size:20");
			
			AdministratorDAO adminDAO = new AdministratorDAO();
			Administrator admin = adminDAO.administratorInfoSelect(id);
			
			String no = Integer.toString(admin.getNo());
			lValNo.setText(no);
			lValName.setText(admin.getName());
			lValId.setText(admin.getId());
			lValPassword.setText(admin.getPassword());
			
			GridPane grid = new GridPane();
			grid.addColumn(0, lNo, lName, lId, lPassword);
			grid.addColumn(1, label1, label2, label3, label4);
			grid.addColumn(2, lValNo, lValName, lValId, lValPassword);
			grid.setAlignment(Pos.CENTER);
			grid.setVgap(30); grid.setHgap(50);
			
			Button btnLogOut = new Button("로그아웃");
			btnLogOut.setPrefSize(170, 40);
			
			btnLogOut.setOnAction(e2 -> {
				stage.close();
				stage.setScene(logInScene);
				stage.show();
			});
			
			StackPane stack = new StackPane();
			StackPane.setMargin(grid, new Insets(50, 250, 120, 200));
			StackPane.setMargin(btnLogOut, new Insets(0, 70, 60, 0));
			stack.setPrefSize(317, 267);
			StackPane.setAlignment(btnLogOut, Pos.BOTTOM_CENTER);
			stack.getChildren().addAll(grid, btnLogOut);
			pane.setCenter(stack);
		});
		
        Label lblTime = new Label();
    	Boolean stop = false;
    	Thread thread = new Thread() {
    		@Override
    		public void run() {
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd E HH:mm", Locale.KOREA);
    			while(!stop) {
    				String strTime = sdf.format(new Date());
    				Platform.runLater(()->{
    					lblTime.setText(strTime);
    				});
    				try { Thread.sleep(100); } catch (InterruptedException e) {}
    			}
    		};
    	};
    	thread.setDaemon(true);
    	thread.start();
    	
        BorderPane.setAlignment(lblTime, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(lblTime, new Insets(5, 5, 0, 0));
        pane.setBottom(lblTime);
        
		pane.setStyle("-fx-font-family: 'Malgun Gothic';");
		adminScene = new Scene(pane, 900, 500);
		stage.setScene(adminScene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

// 관리자 계정 서브 페이지
class AdminWorkStage {
	
	static Label lId = new Label("     I     D   : ");
	static Label lPassword = new Label(" Password  : ");
	static Label lStudentId = new Label("   학     번   : ");
	static Label lName = new Label("   이     름   : ");
	static Label lDepartment = new Label("   학     과   : ");
	static Label lAddress = new Label("   주     소   : ");
	static Label lPhoneNumber = new Label(" 전 화 번 호 : ");
	static TextField tfStudentId = new TextField();
	static TextField tfName = new TextField();
	static TextField tfId = new TextField();
	static Text txt = new Text();
	static PasswordField tfPassword = new PasswordField();
	static ComboBox<String> cbDepartment;
	static TextField tfAddress = new TextField();
	static TextField tfPhoneNumber = new TextField();
	
	static Boolean stuIsCheck = false;
	static Boolean profIsCheck = false;
	static String prevId = "";
	
	static Stage stage = new Stage();
	
	// 관리자 - 학생 계정 추가
	public static void adminInsertStuInfo() {
		tfStudentId.setPromptText("숫자만 입력 가능");
		DepartmentDAO department = new DepartmentDAO();
		List<String> departments = department.departmentSignUpSelect();
		ObservableList<String> lstDepartment = FXCollections.observableArrayList(departments);
 
		cbDepartment = new ComboBox<String>(lstDepartment); // 저장한 list값을 학과선택comboBox에 출력
 
		tfPhoneNumber.setPromptText("'-'없이 입력");
		Button btnStuIdCheck = new Button("중복 확인");
		Button btnAddInfo = new Button("추 가");
		btnAddInfo.setPrefSize(60, 20);
		
		GridPane grid = new GridPane();
    	grid.addColumn(0, lStudentId, lName, lDepartment, lId, txt, lPassword, lAddress, lPhoneNumber);
    	grid.addColumn(1, tfStudentId, tfName, cbDepartment, tfId, btnStuIdCheck, tfPassword, tfAddress, tfPhoneNumber);
    	grid.add(btnAddInfo, 1, 9);
    	grid.setVgap(10);
    	grid.setAlignment(Pos.CENTER);
		
		Alert alert = new Alert(AlertType.NONE,"" , ButtonType.OK);
		alert.setTitle("학생 정보 추가");
		
		btnStuIdCheck.setOnAction(e -> { // 학생 회원가입 ID중복 체크버튼 클릭 시
			StudentDAO student = new StudentDAO();
			int count = student.studentIdCheckSelect(tfId.getText()); // 입력한 ID와 DB에서 가져온 ID를 비교하여 중복되는 데이터 수 저장
			Predicate<Integer> checked = (i) -> i == 0; // i가 0이면 true 반환
			stuIsCheck = checked.test(count); // i에 count를 입력하여 반환값을 bool형으로 저장
			
			if(stuIsCheck) { // stuIsCheck가 true일 시 즉, count가 0일 시 즉, 중복된 데이터가 없을 시
				alert.setContentText("사용 가능한 ID 입니다.");
				alert.showAndWait();
			} else if(count == -1) { // 입력한 ID가 없을 시 -1 반환 (studentIdCheckSelect 메소드 참조)
				alert.setContentText("ID를 입력해 주세요.");
				alert.showAndWait();
			} else { // stuIsCheck가 false일 시 즉, count가 1 이상일 시 즉, 중복된 데이터가 있을 시
				alert.setContentText("중복된 ID 입니다.\n다른 ID를 입력해 주세요.");
				alert.showAndWait();
			}
			prevId = tfId.getText(); // 중복체크하는 순간 현재 입력한 ID값 저장
		});
 
		btnAddInfo.setOnAction(e -> { // 추가 버튼 클릭 시
			if(tfId.getText().equals("")) {
				alert.setContentText("ID를 입력해 주세요.");
				alert.showAndWait();
			} else if(tfPassword.getText().equals("")) {
				alert.setContentText("Password를 입력해 주세요.");
				alert.showAndWait();
			} else if(tfStudentId.getText().equals("")) {
				alert.setContentText("학번을 입력해 주세요.");
				alert.showAndWait();
			} else if(tfName.getText().equals("")) {
				alert.setContentText("이름을 입력해 주세요.");
				alert.showAndWait();
			} else if(cbDepartment.getValue() == null) {
				alert.setContentText("학과를 선택해 주세요.");
				alert.showAndWait();
			} else if(stuIsCheck && prevId.equals(tfId.getText())) {
				StudentDAO student = new StudentDAO();
				// 학번 데이터타입을 int형으로 변환
				int student_id = Integer.parseInt(tfStudentId.getText());
				int department_no = department.departmentNoSelect(cbDepartment.getValue());
				// 학생 정보 DB에 회원가입 정보 저장
				student.studentSignUpInsert(student_id, tfId.getText(), tfPassword.getText(), tfName.getText(), department_no, tfAddress.getText(), tfPhoneNumber.getText());
				alert.setTitle("추가 완료");
				alert.setContentText("학생 정보가 정상적으로 추가되었습니다.");
				alert.showAndWait();
			} else {
				alert.setContentText("ID 중복확인을 해 주세요.");
	    		alert.showAndWait();
			}
		});
		
		Scene adminInsertStuInfoScene = new Scene(grid, 350, 400);
		
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("관리자 - 학생 계정 추가");
		stage.setScene(adminInsertStuInfoScene);
		stage.show();
	}
	
	// 관리자 - 학생 계정 수정
	public static void adminUpdateStuInfo(int studentId, String name, String department_name, String id, String password, String address, String phoneNumber) {
		
		String stuId = Integer.toString(studentId);
		tfStudentId.setText(stuId);
		tfName.setText(name);
		tfId.setText(id);
		tfPassword.setText(password);
		tfAddress.setText(address);
		tfPhoneNumber.setText(phoneNumber);
		
		tfStudentId.setDisable(true);
		DepartmentDAO department = new DepartmentDAO();
		List<String> departments = department.departmentSignUpSelect();
		ObservableList<String> lstDepartment = FXCollections.observableArrayList(departments);
 
        cbDepartment = new ComboBox<String>(lstDepartment); // 저장한 list값을 학과선택comboBox에 출력
        cbDepartment.setValue(department_name);
        
		TextField tfAddress = new TextField();
		TextField tfPhoneNumber = new TextField();
		tfPhoneNumber.setPromptText("'-'없이 입력");
		Button btnStuIdCheck = new Button("중복 확인");
		Button btnReviseInfo = new Button("수 정");
		btnReviseInfo.setPrefSize(60, 20);
		
		GridPane grid = new GridPane();
		grid.addColumn(0, lStudentId, lName, lDepartment, lId, txt, lPassword, lAddress, lPhoneNumber);
    	grid.addColumn(1, tfStudentId, tfName, cbDepartment, tfId, btnStuIdCheck, tfPassword, tfAddress, tfPhoneNumber);
    	grid.add(btnReviseInfo, 1, 9);
    	grid.setVgap(10);
    	grid.setAlignment(Pos.CENTER);
		
		Alert alert = new Alert(AlertType.NONE,"" , ButtonType.OK);
		alert.setTitle("학생 정보 수정");
		
		btnStuIdCheck.setOnAction(e -> { // 학생 계정 추가 ID중복 체크버튼 클릭 시
			StudentDAO student = new StudentDAO();
			int count = student.studentIdCheckSelect(tfId.getText()); // 입력한 ID와 DB에서 가져온 ID를 비교하여 중복되는 데이터 수 저장
			Predicate<Integer> checked = (i) -> i == 0; // i가 0이면 true 반환
			stuIsCheck = checked.test(count); // i에 count를 입력하여 반환값을 bool형으로 저장
			
			if(stuIsCheck) { // stuIsCheck가 true일 시 즉, count가 0일 시 즉, 중복된 데이터가 없을 시
				alert.setContentText("사용 가능한 ID 입니다.");
				alert.showAndWait();
			} else if(count == -1) { // 입력한 ID가 없을 시 -1 반환 (studentIdCheckSelect 메소드 참조)
				alert.setContentText("ID를 입력해 주세요.");
				alert.showAndWait();
			} else { // stuIsCheck가 false일 시 즉, count가 1 이상일 시 즉, 중복된 데이터가 있을 시
				alert.setContentText("중복된 ID 입니다.\n다른 ID를 입력해 주세요.");
				alert.showAndWait();
			}
			prevId = tfId.getText(); // 중복체크하는 순간 현재 입력한 ID값 저장
		});
 
		btnReviseInfo.setOnAction(e -> { // 수정 버튼 클릭 시
			if(tfId.getText().equals(id))
				stuIsCheck = true;
				prevId = tfId.getText();
			
			if(tfId.getText().equals("")) {
				alert.setContentText("ID를 입력해 주세요.");
				alert.showAndWait();
			} else if(tfPassword.getText().equals("")) {
				alert.setContentText("Password를 입력해 주세요.");
				alert.showAndWait();
			} else if(tfName.getText().equals("")) {
				alert.setContentText("이름을 입력해 주세요.");
				alert.showAndWait();
			} else if(stuIsCheck && prevId.equals(tfId.getText())) {
		    	alert.setHeaderText("학생 계정 수정");
		    	alert.setContentText("학번 : " + studentId + "\n"
		    							+ "이름 : " + tfName.getText() + "\n"
		    							+ "학과 : " + cbDepartment.getValue() + "\n"
		    							+ "ID : " + tfId.getText() + "\n"
		    							+ "Password : " + tfPassword.getText() + "\n"
		    							+ "주소 : " + tfAddress.getText() + "\n"
		    							+ "연락처 : " + tfPhoneNumber.getText() + "\n"
		    							+ "학생 계정을 수정하시겠습니까?");
		    	ButtonType btnOk = new ButtonType("Yes", ButtonBar.ButtonData.YES);
		    	ButtonType btnCancel = new ButtonType("No", ButtonBar.ButtonData.NO);
		    	alert.getButtonTypes().setAll(btnOk, btnCancel);
		    	alert.showAndWait().ifPresent(type -> {
		    		if(type.equals(btnOk)) {
		    			AdministratorDAO admin = new AdministratorDAO();
						int student_Id = Integer.parseInt(tfStudentId.getText());
						int department_no = department.departmentNoSelect(cbDepartment.getValue());
						admin.studentInfoUpdate(student_Id, tfName.getText(), department_no, tfId.getText(), tfPassword.getText(), tfAddress.getText(), tfPhoneNumber.getText());
		    			Alert alert2 = new Alert(AlertType.NONE,"" , ButtonType.OK);
						alert2.setTitle("수정 완료");
						alert2.setContentText("학생 정보가 정상적으로 수정되었습니다.");
		    			alert2.showAndWait();
		    		} else {}
		    	});
			} else {
				alert.setContentText("ID 중복확인을 해 주세요.");
	    		alert.showAndWait();
			}
		});
		
		Scene adminUpdateStuInfoScene = new Scene(grid, 350, 400);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("관리자 - 학생 계정 수정");
		stage.setScene(adminUpdateStuInfoScene);
		stage.show();
	}
	
	// 관리자 - 교수 계정 추가
	public static void adminInsertProfInfo() {
		DepartmentDAO department = new DepartmentDAO();
		List<String> departments = department.departmentSignUpSelect();
		ObservableList<String> lstDepartment = FXCollections.observableArrayList(departments);
		cbDepartment = new ComboBox<String>(lstDepartment); // 저장한 list값을 학과선택comboBox에 출력
		tfPhoneNumber.setPromptText("'-'없이 입력");
		Button btnProfIdCheck = new Button("중복 확인");
		Button btnAddInfo = new Button("추 가");
		btnAddInfo.setPrefSize(60, 20);
		
		GridPane grid = new GridPane();
    	grid.addColumn(0, lName, lDepartment, lId, txt, lPassword, lAddress, lPhoneNumber);
    	grid.addColumn(1, tfName, cbDepartment, tfId, btnProfIdCheck, tfPassword, tfAddress, tfPhoneNumber);
    	grid.add(btnAddInfo, 1, 9);
    	grid.setVgap(10);
    	grid.setAlignment(Pos.CENTER);
		
		Alert alert = new Alert(AlertType.NONE,"" , ButtonType.OK);
		alert.setTitle("교수 정보 추가");
		
		btnProfIdCheck.setOnAction(e -> { // 교수 계정 추가 ID중복 체크버튼 클릭 시
			ProfessorDAO professor = new ProfessorDAO();
			int count = professor.professorIdCheckSelect(tfId.getText()); // 입력한 ID와 DB에서 가져온 ID를 비교하여 중복되는 데이터 수 저장
			Predicate<Integer> checked = (i) -> i == 0; // i가 0이면 true 반환
			profIsCheck = checked.test(count); // i에 count를 입력하여 반환값을 bool형으로 저장
			
			if(profIsCheck) { // profIsCheck가 true일 시 즉, count가 0일 시 즉, 중복된 데이터가 없을 시
				alert.setContentText("사용 가능한 ID 입니다.");
				alert.showAndWait();
			} else if(count == -1) { // 입력한 ID가 없을 시 -1 반환 (professorIdCheckSelect 메소드 참조)
				alert.setContentText("ID를 입력해 주세요.");
				alert.showAndWait();
			} else { // profIsCheck가 false일 시 즉, count가 1 이상일 시 즉, 중복된 데이터가 있을 시
				alert.setContentText("중복된 ID 입니다.\n다른 ID를 입력해 주세요.");
				alert.showAndWait();
			}
			prevId = tfId.getText(); // 중복체크하는 순간 현재 입력한 ID값 저장
		});
 
		btnAddInfo.setOnAction(e -> { // 추가 버튼 클릭 시
			if(tfId.getText().equals("")) {
				alert.setContentText("ID를 입력해 주세요.");
				alert.showAndWait();
			} else if(tfPassword.getText().equals("")) {
				alert.setContentText("Password를 입력해 주세요.");
				alert.showAndWait();
			} else if(tfName.getText().equals("")) {
				alert.setContentText("이름을 입력해 주세요.");
				alert.showAndWait();
			} else if(cbDepartment.getValue() == null) {
				alert.setContentText("학과를 선택해 주세요.");
				alert.showAndWait();
			} else if(profIsCheck && prevId.equals(tfId.getText())) {
				ProfessorDAO professor = new ProfessorDAO();
				int department_no = department.departmentNoSelect(cbDepartment.getValue());
				// 교수 정보 DB에 회원가입 정보 저장
				professor.professorSignUpInsert(tfId.getText(), tfPassword.getText(), tfName.getText(), department_no, tfAddress.getText(), tfPhoneNumber.getText());
				alert.setTitle("추가 완료");
				alert.setContentText("교수 정보가 정상적으로 추가되었습니다.");
				alert.showAndWait();
			} else {
				alert.setContentText("ID 중복확인을 해 주세요.");
	    		alert.showAndWait();
			}
		});
		
		Scene adminInsertProfInfoScene = new Scene(grid, 350, 400);
		
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("관리자 - 교수 계정 추가");
		stage.setScene(adminInsertProfInfoScene);
		stage.show();
	}
	
	// 관리자 - 교수 계정 수정
	public static void adminUpdateProfInfo(String name, String department_name, String id, String password, String address, String phoneNumber) {
		
		tfId.setText(id);
		tfPassword.setText(password);
		tfName.setText(name);
		tfAddress.setText(address);
		tfPhoneNumber.setText(phoneNumber);
		
		tfId.setDisable(true);
		DepartmentDAO department = new DepartmentDAO();
		List<String> departments = department.departmentSignUpSelect();
		ObservableList<String> lstDepartment = FXCollections.observableArrayList(departments);
 
        cbDepartment = new ComboBox<String>(lstDepartment); // 저장한 list값을 학과선택comboBox에 출력
        cbDepartment.setValue(department_name);
        
		TextField tfAddress = new TextField();
		TextField tfPhoneNumber = new TextField();
		tfPhoneNumber.setPromptText("'-'없이 입력");
		Button btnReviseInfo = new Button("수 정");
		btnReviseInfo.setPrefSize(60, 20);
		
		GridPane grid = new GridPane();
		grid.addColumn(0, lId, lPassword, lName, lDepartment, lAddress, lPhoneNumber);
    	grid.addColumn(1, tfId, tfPassword, tfName, cbDepartment, tfAddress, tfPhoneNumber);
    	grid.add(btnReviseInfo, 1, 9);
    	grid.setVgap(10);
    	grid.setAlignment(Pos.CENTER);
		
		Alert alert = new Alert(AlertType.NONE,"" , ButtonType.OK);
		alert.setTitle("교수 정보 수정");
 
		btnReviseInfo.setOnAction(e -> { // 수정 버튼 클릭 시
			if(tfPassword.getText().equals("")) {
				alert.setContentText("Password를 입력해 주세요.");
				alert.showAndWait();
			} else if(tfName.getText().equals("")) {
				alert.setContentText("이름을 입력해 주세요.");
				alert.showAndWait();
			} else {
		    	alert.setHeaderText("교수 계정 수정");
		    	alert.setContentText("이름 : " + tfName.getText() + "\n"
		    							+ "학과 : " + cbDepartment.getValue() + "\n"
		    							+ "ID : " + tfId.getText() + "\n"
		    							+ "Password : " + tfPassword.getText() + "\n"
		    							+ "주소 : " + tfAddress.getText() + "\n"
		    							+ "연락처 : " + tfPhoneNumber.getText() + "\n"
		    							+ "교수 계정을 수정하시겠습니까?");
		    	ButtonType btnOk = new ButtonType("Yes", ButtonBar.ButtonData.YES);
		    	ButtonType btnCancel = new ButtonType("No", ButtonBar.ButtonData.NO);
		    	alert.getButtonTypes().setAll(btnOk, btnCancel);
		    	alert.showAndWait().ifPresent(type -> {
		    		if(type.equals(btnOk)) {
		    			AdministratorDAO admin = new AdministratorDAO();
						int department_no = department.departmentNoSelect(cbDepartment.getValue());
						admin.professorInfoUpdate(tfName.getText(), department_no, tfId.getText(), tfPassword.getText(), tfAddress.getText(), tfPhoneNumber.getText());
		    			Alert alert2 = new Alert(AlertType.NONE,"" , ButtonType.OK);
						alert2.setTitle("수정 완료");
						alert2.setContentText("교수 정보가 정상적으로 수정되었습니다.");
		    			alert2.showAndWait();
		    		} else {}
		    	});
			}
		});
		
		Scene adminUpdateProfInfoScene = new Scene(grid, 350, 400);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("관리자 - 교수 계정 수정");
		stage.setScene(adminUpdateProfInfoScene);
		stage.show();
	}
	
}