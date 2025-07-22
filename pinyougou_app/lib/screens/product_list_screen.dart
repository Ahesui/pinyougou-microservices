import 'package:flutter/material.dart';
import '../models/product.dart';
import '../services/api_service.dart';

class ProductListScreen extends StatefulWidget {
  @override
  _ProductListScreenState createState() => _ProductListScreenState();
}

class _ProductListScreenState extends State<ProductListScreen> {
  final ApiService _apiService = ApiService();
  final List<Product> _products = [];
  final ScrollController _scrollController = ScrollController();

  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;

  @override
  void initState() {
    super.initState();
    _fetchProducts(); // 首次加载数据

    // 添加滚动监听
    _scrollController.addListener(() {
      if (_scrollController.position.pixels ==
              _scrollController.position.maxScrollExtent &&
          !_isLoading &&
          _hasMore) {
        _fetchProducts(); // 滚动到底部时，加载更多
      }
    });
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  Future<void> _fetchProducts() async {
    setState(() {
      _isLoading = true;
    });

    final pageData = await _apiService.getProducts(page: _currentPage);
    if (pageData.isNotEmpty) {
      final List<dynamic> content = pageData['content'];
      setState(() {
        _products.addAll(
          content.map((item) => Product.fromJson(item)).toList(),
        );
        _currentPage++;
        _hasMore = !pageData['last']; // 从后端获取是否是最后一页
      });
    }

    setState(() {
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('商品列表')),
      body: ListView.builder(
        controller: _scrollController,
        itemCount: _products.length + (_hasMore ? 1 : 0), // 如果还有更多，则多显示一个加载项
        itemBuilder: (context, index) {
          if (index == _products.length) {
            return Center(child: CircularProgressIndicator()); // 列表底部的加载动画
          }
          final product = _products[index];
          return ListTile(
            title: Text(product.productName),
            subtitle: Text('¥ ${product.price.toStringAsFixed(2)}'),
            onTap: () {
              /* 跳转详情页 */
            },
          );
        },
      ),
    );
  }
}
