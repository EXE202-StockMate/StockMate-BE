CREATE TABLE "FinishProduct"(
    "fgID" NVARCHAR(255) NOT NULL,
    "name" NVARCHAR(255) NOT NULL,
    "description" TEXT NOT NULL,
    "image" NVARCHAR(255) NOT NULL,
    "category" NVARCHAR(255) NOT NULL,
    "dimension" NVARCHAR(255) NOT NULL,
    "createDate" DATETIME NOT NULL,
    "updateDate" DATETIME NOT NULL,
    "status" INT NOT NULL
);
ALTER TABLE
    "FinishProduct" ADD CONSTRAINT "finishproduct_fgid_primary" PRIMARY KEY("fgID");
CREATE TABLE "Semi-finishProduct"(
    "sfgID" NVARCHAR(255) NOT NULL,
    "name" NVARCHAR(255) NOT NULL,
    "description" TEXT NOT NULL,
    "image" NVARCHAR(255) NOT NULL,
    "dimension" NVARCHAR(255) NOT NULL,
    "createDate" DATETIME NOT NULL,
    "updateDate" DATETIME NOT NULL,
    "status" INT NOT NULL
);
ALTER TABLE
    "Semi-finishProduct" ADD CONSTRAINT "semi_finishproduct_sfgid_primary" PRIMARY KEY("sfgID");
CREATE TABLE "Role"(
    "roleID" BIGINT NOT NULL,
    "name" NVARCHAR(255) NOT NULL,
    "description" TEXT NOT NULL,
    "status" INT NOT NULL
);
ALTER TABLE
    "Role" ADD CONSTRAINT "role_roleid_primary" PRIMARY KEY("roleID");
CREATE TABLE "User"(
    "userID" NVARCHAR(255) NOT NULL,
    "fullName" NVARCHAR(255) NOT NULL,
    "phoneNumber" NVARCHAR(255) NOT NULL,
    "email" NVARCHAR(255) NOT NULL,
    "password" NVARCHAR(255) NOT NULL,
    "image" NVARCHAR(255) NOT NULL,
    "createDate" DATETIME NOT NULL,
    "updateDate" DATETIME NOT NULL,
    "status" NVARCHAR(255) NOT NULL,
    "roleID" INT NULL,
    "managerID" NVARCHAR(255) NOT NULL
);
ALTER TABLE
    "User" ADD CONSTRAINT "user_userid_primary" PRIMARY KEY("userID");
CREATE TABLE "RawMaterial"(
    "rmID" NVARCHAR(255) NOT NULL,
    "name" NVARCHAR(255) NOT NULL,
    "description" TEXT NOT NULL,
    "image" NVARCHAR(255) NOT NULL,
    "category" NVARCHAR(255) NOT NULL,
    "dimension" NVARCHAR(255) NOT NULL,
    "createDate" DATETIME NOT NULL,
    "updateDate" DATETIME NOT NULL,
    "status" INT NOT NULL
);
ALTER TABLE
    "RawMaterial" ADD CONSTRAINT "rawmaterial_rmid_primary" PRIMARY KEY("rmID");
CREATE TABLE "Stock"(
    "stockID" INT NOT NULL,
    "rmID" NVARCHAR(255) NOT NULL,
    "fgID" NVARCHAR(255) NOT NULL,
    "sfgID" NVARCHAR(255) NOT NULL,
    "quantity" INT NOT NULL,
    "unit" NVARCHAR(255) NOT NULL,
    "status" NVARCHAR(255) NOT NULL
);
ALTER TABLE
    "Stock" ADD CONSTRAINT "stock_stockid_primary" PRIMARY KEY("stockID");
CREATE TABLE "BOMHeader"(
    "headerID" INT NOT NULL,
    "fgID" NVARCHAR(255) NOT NULL,
    "sfgID" NVARCHAR(255) NOT NULL,
    "note" TEXT NOT NULL
);
ALTER TABLE
    "BOMHeader" ADD CONSTRAINT "bomheader_headerid_primary" PRIMARY KEY("headerID");
CREATE TABLE "BOMItem"(
    "itemID" INT NOT NULL,
    "headerID" INT NOT NULL,
    "materialID" NVARCHAR(255) NOT NULL,
    "quantity" INT NOT NULL,
    "unit" NVARCHAR(255) NOT NULL,
    "note" TEXT NOT NULL
);
ALTER TABLE
    "BOMItem" ADD CONSTRAINT "bomitem_itemid_primary" PRIMARY KEY("itemID");
CREATE TABLE "Request"(
    "requestID" NVARCHAR(255) NOT NULL,
    "type" NVARCHAR(255) NOT NULL,
    "orderID" NVARCHAR(255) NOT NULL,
    "materialID" NVARCHAR(255) NOT NULL,
    "shortageID" NVARCHAR(255) NOT NULL,
    "userID" NVARCHAR(255) NOT NULL,
    "quantity" INT NOT NULL,
    "unit" NVARCHAR(255) NOT NULL,
    "createDate" DATETIME NOT NULL,
    "updateDate" DATETIME NOT NULL,
    "status" NVARCHAR(255) NOT NULL,
    "note" TEXT NOT NULL
);
ALTER TABLE
    "Request" ADD CONSTRAINT "request_requestid_primary" PRIMARY KEY("requestID");
CREATE TABLE "Order"(
    "orderID" NVARCHAR(255) NOT NULL,
    "userID" NVARCHAR(255) NOT NULL,
    "customer" NVARCHAR(255) NOT NULL,
    "createDate" DATETIME NOT NULL,
    "updateDate" DATETIME NOT NULL,
    "status" NVARCHAR(255) NOT NULL
);
ALTER TABLE
    "Order" ADD CONSTRAINT "order_orderid_primary" PRIMARY KEY("orderID");
CREATE TABLE "OrderItem"(
    "itemID" INT NOT NULL,
    "orderID" NVARCHAR(255) NOT NULL,
    "materialID" NVARCHAR(255) NOT NULL,
    "quantity" INT NOT NULL
);
ALTER TABLE
    "OrderItem" ADD CONSTRAINT "orderitem_itemid_primary" PRIMARY KEY("itemID");
CREATE TABLE "Shortage"(
    "shortageID" INT NOT NULL,
    "orderID" NVARCHAR(255) NOT NULL,
    "rmID" NVARCHAR(255) NOT NULL,
    "quantity" INT NOT NULL,
    "unit" NVARCHAR(255) NOT NULL,
    "createDate" DATETIME NOT NULL,
    "updateDate" DATETIME NOT NULL,
    "status" NVARCHAR(255) NOT NULL
);
ALTER TABLE
    "Shortage" ADD CONSTRAINT "shortage_shortageid_primary" PRIMARY KEY("shortageID");
ALTER TABLE
    "FinishProduct" ADD CONSTRAINT "finishproduct_status_foreign" FOREIGN KEY("status") REFERENCES "OrderItem"("quantity");
ALTER TABLE
    "RawMaterial" ADD CONSTRAINT "rawmaterial_image_foreign" FOREIGN KEY("image") REFERENCES "Stock"("stockID");
ALTER TABLE
    "Order" ADD CONSTRAINT "order_status_foreign" FOREIGN KEY("status") REFERENCES "Shortage"("shortageID");
ALTER TABLE
    "Semi-finishProduct" ADD CONSTRAINT "semi_finishproduct_createdate_foreign" FOREIGN KEY("createDate") REFERENCES "BOMHeader"("headerID");
ALTER TABLE
    "RawMaterial" ADD CONSTRAINT "rawmaterial_description_foreign" FOREIGN KEY("description") REFERENCES "BOMItem"("itemID");
ALTER TABLE
    "Shortage" ADD CONSTRAINT "shortage_rmid_foreign" FOREIGN KEY("rmID") REFERENCES "Request"("status");
ALTER TABLE
    "Stock" ADD CONSTRAINT "stock_unit_foreign" FOREIGN KEY("unit") REFERENCES "FinishProduct"("name");
ALTER TABLE
    "Shortage" ADD CONSTRAINT "shortage_orderid_foreign" FOREIGN KEY("orderID") REFERENCES "RawMaterial"("rmID");
ALTER TABLE
    "Order" ADD CONSTRAINT "order_customer_foreign" FOREIGN KEY("customer") REFERENCES "Request"("materialID");
ALTER TABLE
    "BOMHeader" ADD CONSTRAINT "bomheader_note_foreign" FOREIGN KEY("note") REFERENCES "BOMItem"("headerID");
ALTER TABLE
    "User" ADD CONSTRAINT "user_managerid_foreign" FOREIGN KEY("managerID") REFERENCES "Order"("orderID");
ALTER TABLE
    "Order" ADD CONSTRAINT "order_userid_foreign" FOREIGN KEY("userID") REFERENCES "OrderItem"("itemID");
ALTER TABLE
    "Semi-finishProduct" ADD CONSTRAINT "semi_finishproduct_description_foreign" FOREIGN KEY("description") REFERENCES "BOMItem"("unit");
ALTER TABLE
    "Role" ADD CONSTRAINT "role_roleid_foreign" FOREIGN KEY("roleID") REFERENCES "User"("userID");
ALTER TABLE
    "Stock" ADD CONSTRAINT "stock_rmid_foreign" FOREIGN KEY("rmID") REFERENCES "Semi-finishProduct"("description");
ALTER TABLE
    "FinishProduct" ADD CONSTRAINT "finishproduct_dimension_foreign" FOREIGN KEY("dimension") REFERENCES "BOMHeader"("note");
ALTER TABLE
    "User" ADD CONSTRAINT "user_email_foreign" FOREIGN KEY("email") REFERENCES "Request"("requestID");