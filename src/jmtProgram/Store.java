package jmtProgram;

public class Store {
	
	private String storeName;
	private String storePhone;
	private int productNo;
	private String productName;
	private int price;
	private int stock;
	

	public Store(int productNo, String storeName, String storePhone, String productName, int price, int stock) {
		this.productNo = productNo;
		this.storeName = storeName;
		this.storePhone = storePhone;
		this.productName = productName;
		this.price = price;
		this.stock = stock;
	}

	public String getStoreName() {return storeName;}
	public void setStoreName(String storeName) {this.storeName = storeName;}

	public String getStorePhone() {return storePhone;}
	public void setStorePhone(String storePhone) {this.storePhone = storePhone;}

	public int getProductNo() {return productNo;}
	public void setProductNo(int productNo) {this.productNo = productNo;}

	public String getProductName() {return productName;}
	public void setProductName(String productName) {this.productName = productName;}

	public int getPrice() {return price;}
	public void setPrice(int price) {this.price = price;}

	public int getStock() {return stock;}
	public void setStock(int stock) {this.stock = stock;}

	public String toStoreString() {
		System.out.println("―".repeat(89)+"\n매장명\t전화번호");
		return storeName + "\t" + storePhone;
	}
	
	@Override
	public String toString() {
		System.out.println("―".repeat(89)+"\n매장명\t상품번호\t상품명\t가격\t재고");
		return storeName + "\t" + productNo + "\t" + productName + "\t" + price + "\t" + stock;
	}
}
