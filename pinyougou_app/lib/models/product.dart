class Product {
  final int id;
  final String productName;
  final double price;

  Product({required this.id, required this.productName, required this.price});

  factory Product.fromJson(Map<String, dynamic> json) {
    return Product(
      id: json['id'],
      productName: json['productName'],
      price: (json['price'] as num).toDouble(),
    );
  }
}