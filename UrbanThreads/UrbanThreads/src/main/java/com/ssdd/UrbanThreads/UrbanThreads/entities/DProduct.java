package com.ssdd.UrbanThreads.UrbanThreads.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "products")
public class DProduct {
    @Getter
    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @Getter
    @Column(name = "name")
    private String name;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "category_id")
    private DCategory category;

    @Setter
    @Getter
    @Column (name = "price")
    private double price;

    @Lob
    @Column (name = "photo")
    private Blob photo;

    @Setter
    @Getter
    @Column (name = "description")
    private String description;


    /*@Column (name ="availableSizes")
    private Map<Size,Integer> availableSizes;*/
    public DProduct() {
    }

    // Constructor con parámetros
    public DProduct(String name, DCategory category) {
        this.name = name;
        this.category = category;
    }

    @Setter
    @Getter
    @ElementCollection
    @CollectionTable(name = "product_sizes", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "quantity")
    private Map<Size, Integer> availableSizes;

    public DProduct (String name, DCategory category, double price,Blob photo,String description, Map<Size, Integer>as) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.photo = photo;
        this.description = description;
        this.availableSizes = as;
    }

    public DProduct (String name, DCategory category, double price,String photo,String description, Map<Size, Integer>as) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.setPhoto(photo);
        this.description = description;
        this.availableSizes = as;
    }

    public void setPhoto(String photo) {
        try {
            byte[] bytes = photo.getBytes();

            InputStream inputStream = new ByteArrayInputStream(bytes);

            this.photo = new SerialBlob(bytes);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String getPhotoPath() {
       Blob blob = this.photo;
        try {
            InputStream inputStream = blob.getBinaryStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return new String(outputStream.toByteArray(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] getBlobData() {
            try {
                if (photo != null && photo.length() > 0) {
                    return photo.getBytes(1, (int) photo.length());
                } else {
                    return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

    public Blob getPhoto() {
        return this.photo;
    }
}


