package appControl;

import DTO.CMD4ReturnBundle;
import DTO.InstructionDTO;
import DTO.StockDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import menuScreen.MenuScreenController;

import outputUsers.OutputUsersScreenController;
import SystemEngine.MarketManager;
import SystemEngine.StocksTradeSystem;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import outputUsers.singleUserTab.SingleUserTabController;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ApplicationControl {
    @FXML private TabPane bodyComponent;
    @FXML private OutputUsersScreenController bodyComponentController;

    @FXML private AnchorPane topComponent;
    @FXML private MenuScreenController topComponentController;

    @FXML private TextArea textAreaInformingUser;

    StringProperty activeStockInBook = null;
    StringProperty textUserInformationProperty;

    public ApplicationControl() {
        System.out.println("in ApplicationControl ctor");
        marketManager = new MarketManager();

        textUserInformationProperty = new SimpleStringProperty();
    }

    public void initialize() {
        System.out.println("in ApplicationControl initialize()");
        if (bodyComponentController != null && topComponentController != null) {
            topComponentController.setMainController(this);
            bodyComponentController.setMainController(this);

        }
        textAreaInformingUser.textProperty().bind(textUserInformationProperty);
    }

    public boolean loadXMLFile(String path) {
        boolean status = false;
        try {
            marketManager.loadXML(path);
            textUserInformationProperty.setValue("File loaded successfully.");
            createBody();
            status = true;
        } catch (Exception e) {
            //textUserInformationProperty.setValue("There was a problem with the chosen xml. Please retry with a valid one.");
            textUserInformationProperty.setValue(e.getMessage());
        }
        return status;
    }

    private void createBody() {
        try {
            createUserTabs(new ArrayList<SingleUserTabController>(), bodyComponent);

        } catch (IOException e) {
            textUserInformationProperty.setValue(e.getMessage());
        }

    }


    public List<SingleUserTabController> createUserTabs(List<SingleUserTabController> tabList, TabPane tabPaneUsers) throws IOException {
        for (String key : this.marketManager.getUsers().keySet()) {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("../outputUsers/singleUserTab/singleUserTab2.fxml");
            loader.setLocation(url);
            Tab userTab = loader.load();
          //  Tab userTab = new Tab();
           // userTab.setContent(content);


            SingleUserTabController singleTabController = loader.getController();
            tabList.add(singleTabController);

            singleTabController.setMainController(this);
            userTab.setText(key);

            singleTabController.wiringXMLtoTab(marketManager, key);


            tabPaneUsers.getTabs().add(userTab);
        }
        return tabList;
    }





    public StocksTradeSystem getMarketManager() {
        return marketManager;
    }

    private MarketManager marketManager;
    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }
    public Stage getStage() { return primaryStage; }


    public void ExhibitErrorMain(String message) {
        textUserInformationProperty.setValue(message);
    }

    public void TradeCommit(InstructionDTO gatherInstructionDTO, String symbol, boolean buySelected) {
        try {
            StockDTO fetchedStock = marketManager.getSafeStock(symbol);

            CMD4ReturnBundle bundle =  marketManager.operateOnStocks(gatherInstructionDTO, symbol);

            updateUsers(); //Do: reloading
            matchingActionMSG(bundle, buySelected);

        } catch (Exception e) {
            textUserInformationProperty.setValue(e.getMessage());
        }
    }

    private void matchingActionMSG(CMD4ReturnBundle bundle, boolean buySelected) {

      //  boolean isBuy = isCurrentlyBuying();
        String retMSG = "";
        if (bundle.getInsDTO() == null) {

            if (buySelected) {
                retMSG += "Perfect match found!\nSuccessfully acquired the full extent of the request. \n ";
            } else {
                retMSG += "Perfect match found!\nSuccessfully sold the full extent of the request. \n ";
            }

        } else if (bundle.getTransactionsMade().size() != 0) {

            if (buySelected) {
                retMSG += "Partially match found!\nSuccessfully bought some of the request.\n";
            } else {
                retMSG += "Partially match found!\nSuccessfully sold some of the request.\n";
            }

            if (buySelected) {
                retMSG += "\nThis partial buy instruction was been added to the market(the reminder after partially buying some of the stocks): \n";
            } else {
                retMSG += "\nThis partial sale instruction was added to the market(the reminder after partially selling some of the stock): \n";
            }
            retMSG += instructionTimePriceQuantity(bundle.getInsDTO()); //Do: doesn't print
        } else {
            if (buySelected)
                retMSG += "There are no active sale instruction that matches with your request.\n";
            else
                retMSG += "There are no active buy instruction that matches with your request.\n";
            if (buySelected)
                retMSG += "The full buy instruction that has been added to the market. \n";
            else
                retMSG += "The full sale instruction that has been added to the market. \n";
        }
        textUserInformationProperty.setValue(retMSG);
    }

    private void updateUsers() {
//Do: reloading
    }
    public String instructionTimePriceQuantity(InstructionDTO ins) {
        return "Time - " + ins.getTime().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS")) + "\nPrice - " + ins.getPrice() +
                "\nQuantity - " + ins.getQuantity();
    }

}
//    public void updateStocksBookSearchedSymbol(String symbol) {
//    }

//    public void loadComponents() {
//
//    }



//   currentLeftComponentController = leftComponentController;
//   informConnections(leftComponentController,bodyComponentController);
//    private void informConnections(SubController controller, SubScreen screen) {
//        controller.know(screen);
//        screen.know(controller);
//    }
//    public void readXmlToEngine() throws IOException {
//        leftComponentController.submitData();
//        rightComponentController.submitData();
//        bottomComponentController.submitData();
//    }

//    public void requestUpdateTradeOutput(CMD4ReturnBundle bundle) {
//        bodyComponentController.updateTradeScreen(bundle);
//    }
//
//    public void requestUpdateStocksBook(String enteredSymbolText) throws Exception {
//        //if stock found
//        bodyComponentController.updateStocksBookSearchedSymbol(marketManager.getSafeStock(enteredSymbolText), enteredSymbolText);
//        //else error
//    }


// public void requestLoadUI(SubController.Type requested) throws IOException {
//        if (requested.equals(SubController.Type.STOCKS_BOOK)) {
//
//        } else if (requested.equals(SubController.Type.TRADE)) {
//
//
//        } else if (requested.equals(SubController.Type.INTRODUCTION)) {
//
//        }
//
//    }

//    public void requestLoadUI(SubController.Type requested) throws IOException {
//        if (requested.equals(SubController.Type.STOCKS_BOOK)) {
//            AnchorPane control = FXMLLoader.load(getClass().getResource(STOCKSBOOK_CONTROL_RESOURCE));
//            leftComponent.getChildren().setAll(control);
//            currentLeftComponentController = new StocksBookInputScreenController();
//
//        } else if (requested.equals(SubController.Type.TRADE)) {
//            AnchorPane newLeftControl = FXMLLoader.load(getClass().getResource("/stocksBookInputScreen.fxml"));
//
//          //  leftComponent.getChildren().setAll(control);
//            currentLeftComponentController = new TradeInputScreenController(leftComponent);
//
//        } else if (requested.equals(SubController.Type.INTRODUCTION)) {
//            AnchorPane blankControl = new AnchorPane();
//            //put money icon
//            blankControl.setPrefHeight(400);
//            blankControl.setPrefWidth(270);
//            leftComponent.getChildren().setAll(blankControl);
//            currentLeftComponentController = new StocksBookInputScreenController();
//            System.out.println("in introduction");
//
//        }
//        currentLeftComponentController.setMainController(this);
//        currentLeftComponentController.setManager(this.marketManager);
//        currentLeftComponentController.submitData();
//    }