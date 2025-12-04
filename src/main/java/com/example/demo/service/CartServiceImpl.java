package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.dto.request.*;
import com.example.demo.dto.request.ShopDTO;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.interfaceI.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /** Récupérer un panier par id avec items et totaux */
    @Override
    public ApiResponse<CartDTO> getCartById(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));

        // Calcul totalCents pour chaque item
        cart.getItems().forEach(item ->
                item.setTotalCents(item.getQuantity() * item.getProduct().getPriceCents())
        );

        // Calcul total du panier
        int total = cart.getItems().stream().mapToInt(CartItem::getTotalCents).sum();
        cart.setTotalPriceCents(total);

        // Mapping DTO
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setOrdered(cart.isOrdered());
        cartDTO.setCreatedAt(cart.getCreatedAt().toString());
        cartDTO.setUpdatedAt(cart.getUpdatedAt() != null ? cart.getUpdatedAt().toString() : null);
        if (cart.getShop() != null) {
            ShopDTO shopDTO = new ShopDTO();
            shopDTO.setId(cart.getShop().getId());
            shopDTO.setName(cart.getShop().getName());
            cartDTO.setShop(shopDTO);
        }

        cartDTO.setTotalPriceCents(cart.getTotalPriceCents());
        cartDTO.setItems(cart.getItems().stream().map(item -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setId(item.getId());
            dto.setQuantity(item.getQuantity());
            dto.setTotalCents(item.getTotalCents());

            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(item.getProduct().getId());
            productDTO.setName(item.getProduct().getName());
            productDTO.setImageUrl(item.getProduct().getImageUrl());
            productDTO.setPriceCents(item.getProduct().getPriceCents());
            productDTO.setStock(item.getProduct().getStock());
           // productDTO.setCategory(item.getProduct().getCategory().toString());

            if (item.getProduct().getShop() != null) {
                ShopDTO productShop = new ShopDTO();
                productShop.setId(item.getProduct().getShop().getId());
                productShop.setName(item.getProduct().getShop().getName());
                productDTO.setShop(productShop);
            }

            if (item.getProduct().getCategory() != null) {
                CategoryDTO productCategory = new CategoryDTO();
                productCategory.setId(item.getProduct().getCategory().getId());
                productCategory.setName(item.getProduct().getCategory().getName());
                productDTO.setCategory(productCategory);
            }

            dto.setProduct(productDTO);
            return dto;
        }).collect(Collectors.toList()));

        return new ApiResponse<>("Panier récupéré", cartDTO, HttpStatus.OK.value());
    }


    /** Créer un panier et ajouter un premier produit */
    @Override
    public ApiResponse<CartDTO> createCart(CreateCartRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Création du panier
        Cart cart = new Cart();
        cart.setCreatedAt(LocalDateTime.now());
        cart = cartRepository.save(cart);

        // Création du premier article du panier
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setTotalCents(dto.getQuantity() * product.getPriceCents());
        cartItemRepository.save(item);

        // Mise à jour du panier
        cart.setShop(product.getShop());
        cart.setTotalPriceCents(item.getTotalCents());
        cart = cartRepository.save(cart);

        // Création du ProductDTO
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setPriceCents(product.getPriceCents());
        productDTO.setStock(product.getStock());
        productDTO.setCategory(product.getCategory() !=null ? new CategoryDTO(product.getCategory().getId(),product.getCategory().getName()) : null);
        productDTO.setShop(product.getShop() != null ? new ShopDTO(product.getShop().getId(), product.getShop().getName()) : null);

        // Création du CartItemDTO
        CartItemDTO itemDTO = new CartItemDTO();
        itemDTO.setId(item.getId());
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setTotalCents(item.getTotalCents());
        itemDTO.setProduct(productDTO);

        // Création du CartDTO
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setOrdered(cart.isOrdered());
        cartDTO.setCreatedAt(cart.getCreatedAt().toString());
        cartDTO.setUpdatedAt(cart.getUpdatedAt() != null ? cart.getUpdatedAt().toString() : null);
        cartDTO.setShop(cart.getShop() != null ? new ShopDTO(cart.getShop().getId(), cart.getShop().getName()) : null);
        cartDTO.setTotalPriceCents(cart.getTotalPriceCents());
        cartDTO.setItems(List.of(itemDTO));

        return new ApiResponse<>("Panier créé et produit ajouté", cartDTO, HttpStatus.CREATED.value());
    }


    @Override
    public ApiResponse<CartDTO> createEmptyCart() {
        Cart cart = new Cart();
        cart.setCreatedAt(LocalDateTime.now());
        cart.setTotalPriceCents(0);
        cart = cartRepository.save(cart);

        // Mapping DTO minimal
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setOrdered(cart.isOrdered());
        cartDTO.setCreatedAt(cart.getCreatedAt().toString());
        cartDTO.setUpdatedAt(cart.getUpdatedAt().toString());
        cartDTO.setTotalPriceCents(0);
        cartDTO.setItems(List.of()); // La liste est vide

        return new ApiResponse<>("Panier vide créé", cartDTO, HttpStatus.CREATED.value());
    }

    /** Ajouter un produit dans un panier existant */
    @Override
    public ApiResponse<CartDTO> addItemToCart(Long cartId, AddItemRequest request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cartId, request.getProductId());

        CartItem cartItem;
        if (existing.isPresent()) {
            cartItem = existing.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }

        // Calcul totalCents de l'article
        cartItem.setTotalCents(cartItem.getQuantity() * product.getPriceCents());
        cartItemRepository.save(cartItem);

        // Recalcul total du panier
        int total = cart.getItems().stream().mapToInt(CartItem::getTotalCents).sum();
        cart.setTotalPriceCents(total);
        cart.setUpdatedAt(LocalDateTime.now());
        cart.setShop(product.getShop());
        cartRepository.save(cart);

        // Création du ProductDTO
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setPriceCents(product.getPriceCents());
        productDTO.setStock(product.getStock());
        productDTO.setCategory(product.getCategory() !=null ? new CategoryDTO(product.getCategory().getId(),product.getCategory().getName()) : null);
        productDTO.setShop(product.getShop() != null ? new ShopDTO(product.getShop().getId(), product.getShop().getName()) : null);

        // Création du CartItemDTO
        CartItemDTO itemDTO = new CartItemDTO();
        itemDTO.setId(cartItem.getId());
        itemDTO.setQuantity(cartItem.getQuantity());
        itemDTO.setTotalCents(cartItem.getTotalCents());
        itemDTO.setProduct(productDTO);

        // Création du CartDTO
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setOrdered(cart.isOrdered());
        cartDTO.setCreatedAt(cart.getCreatedAt().toString());
        cartDTO.setUpdatedAt(cart.getUpdatedAt() != null ? cart.getUpdatedAt().toString() : null);
        cartDTO.setShop(cart.getShop() != null ? new ShopDTO(cart.getShop().getId(), cart.getShop().getName()) : null);
        cartDTO.setTotalPriceCents(cart.getTotalPriceCents());
        cartDTO.setItems(List.of(itemDTO));

        return new ApiResponse<>("Produit ajouté au panier", cartDTO, HttpStatus.OK.value());
    }


    /** Modifier la quantité d’un article */
    @Override
    public ApiResponse<CartDTO> updateCartItem(Long cartId, Long itemId, UpdateItemRequest request) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        if (!item.getCart().getId().equals(cartId)) {
            return new ApiResponse<>("Cet article n'appartient pas au panier", null, HttpStatus.BAD_REQUEST.value());
        }

        // Mettre à jour la quantité
        item.setQuantity(request.getQuantity());
        item.setTotalCents(item.getQuantity() * item.getProduct().getPriceCents());
        cartItemRepository.save(item);

        // Mettre à jour le panier
        Cart cart = item.getCart();
        cart.setTotalPriceCents(cart.getItems().stream()
                .mapToInt(i -> i.getQuantity() * i.getProduct().getPriceCents())
                .sum());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        // Mapping vers DTO
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setOrdered(cart.isOrdered());
        cartDTO.setCreatedAt(cart.getCreatedAt().toString());
        cartDTO.setUpdatedAt(cart.getUpdatedAt() != null ? cart.getUpdatedAt().toString() : null);

        if (cart.getShop() != null) {
            ShopDTO shopDTO = new ShopDTO();
            shopDTO.setId(cart.getShop().getId());
            shopDTO.setName(cart.getShop().getName());
            cartDTO.setShop(shopDTO);
        }

        cartDTO.setTotalPriceCents(cart.getTotalPriceCents());
        cartDTO.setItems(cart.getItems().stream().map(i -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setId(i.getId());
            dto.setQuantity(i.getQuantity());
            dto.setTotalCents(i.getTotalCents());

            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(i.getProduct().getId());
            productDTO.setName(i.getProduct().getName());
            productDTO.setImageUrl(i.getProduct().getImageUrl());
            productDTO.setPriceCents(i.getProduct().getPriceCents());
            productDTO.setStock(i.getProduct().getStock());
            //productDTO.setCategory(i.getProduct().getCategory().toString());

            if (i.getProduct().getShop() != null) {
                ShopDTO productShop = new ShopDTO();
                productShop.setId(i.getProduct().getShop().getId());
                productShop.setName(i.getProduct().getShop().getName());
                productDTO.setShop(productShop);
            }

            if (i.getProduct().getCategory() != null) {
                CategoryDTO productCategory = new CategoryDTO();
                productCategory.setId(i.getProduct().getCategory().getId());
                productCategory.setName(i.getProduct().getCategory().getName());
                productDTO.setCategory(productCategory);
            }

            dto.setProduct(productDTO);
            return dto;
        }).collect(Collectors.toList()));

        return new ApiResponse<>("Article mis à jour", cartDTO, HttpStatus.OK.value());
    }


    /** Supprimer un article du panier */
    @Override
    public ApiResponse<CartDTO> removeCartItem(Long cartId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        if (!item.getCart().getId().equals(cartId)) {
            return new ApiResponse<>("Cet article n'appartient pas au panier", null, HttpStatus.BAD_REQUEST.value());
        }

        Cart cart = item.getCart();
        cartItemRepository.delete(item);

        // Recalculer totalPriceCents après suppression (utilise la liste mise à jour par JPA)
        cart.setTotalPriceCents(cart.getItems().stream()
                .mapToInt(i -> i.getQuantity() * i.getProduct().getPriceCents())
                .sum());

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        // Mapping DTO
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setOrdered(cart.isOrdered());
        cartDTO.setCreatedAt(cart.getCreatedAt().toString());
        cartDTO.setUpdatedAt(cart.getUpdatedAt() != null ? cart.getUpdatedAt().toString() : null);

        if (cart.getShop() != null) {
            ShopDTO shopDTO = new ShopDTO();
            shopDTO.setId(cart.getShop().getId());
            shopDTO.setName(cart.getShop().getName());
            cartDTO.setShop(shopDTO);
        }

        cartDTO.setTotalPriceCents(cart.getTotalPriceCents());
        cartDTO.setItems(cart.getItems().stream().map(i -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setId(i.getId());
            dto.setQuantity(i.getQuantity());
            dto.setTotalCents(i.getQuantity() * i.getProduct().getPriceCents());

            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(i.getProduct().getId());
            productDTO.setName(i.getProduct().getName());
            productDTO.setImageUrl(i.getProduct().getImageUrl());
            productDTO.setPriceCents(i.getProduct().getPriceCents());
            productDTO.setStock(i.getProduct().getStock());
            //productDTO.setCategory(i.getProduct().getCategory().toString());

            if (i.getProduct().getShop() != null) {
                ShopDTO productShop = new ShopDTO();
                productShop.setId(i.getProduct().getShop().getId());
                productShop.setName(i.getProduct().getShop().getName());
                productDTO.setShop(productShop);
            }

            if (i.getProduct().getCategory() != null) {
                CategoryDTO productCategory = new CategoryDTO();
                productCategory.setId(i.getProduct().getCategory().getId());
                productCategory.setName(i.getProduct().getCategory().getName());
                productDTO.setCategory(productCategory);
            }
            dto.setProduct(productDTO);
            return dto;
        }).collect(Collectors.toList()));

        return new ApiResponse<>("Article supprimé", cartDTO, HttpStatus.OK.value());
    }


    @Override
    public ApiResponse<CartDTO> clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));

        // ÉTAPE CRUCIALE POUR SUPPRIMER DÉFINITIVEMENT LE PANIER:

        // 2. Supprimer l'entité Cart.
        // Grâce à 'cascade = CascadeType.ALL' et 'orphanRemoval = true' sur la liste 'items'
        // dans l'entité Cart, JPA/Hibernate va automatiquement:
        // a) Supprimer tous les CartItem liés.
        // b) Supprimer l'entité Cart elle-même.
        cartRepository.delete(cart);

        // 3. RETOURNER la réponse.
        // Nous ne pouvons plus manipuler l'objet 'cart' ni le sauvegarder (save)
        // car il est maintenant supprimé de la base de données.

        // Un statut 204 No Content (pas de corps) est idéal pour une suppression,
        // mais si l'API exige un corps, retournez un CartDTO vide.

        // Retour d'une réponse de succès sans données spécifiques
        // HttpStatus.NO_CONTENT.value() est 204, mais on retourne 200/OK pour garder le DTO.
        CartDTO emptyCartDTO = new CartDTO();
        emptyCartDTO.setItems(List.of());
        emptyCartDTO.setTotalPriceCents(0);

        // Notez que l'ID n'est pas inclus car l'entité a été détruite.

        return new ApiResponse<>("Panier supprimé et contenu vidé", emptyCartDTO, HttpStatus.OK.value());

    }

}
