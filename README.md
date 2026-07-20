# 🛡️ Invoice Fraud Detection System

## 📌 Overview

The Invoice Fraud Detection System is a Spring Boot REST API that detects suspicious invoices using predefined fraud detection rules.

It automatically calculates a fraud score, assigns a risk level, records fraud logs, and allows finance users to approve invoices while maintaining a complete audit trail.

---

## 🚀 Features

- User Authentication using Spring Security
- Vendor Management
- Invoice Submission
- Automatic Fraud Score Calculation
- Risk Level Classification
- Fraud Log Generation
- Invoice Approval Workflow
- MySQL Database Integration
- REST APIs

---

## 🛠️ Tech Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Postman

---

## 📂 Project Structure

src
├── config
├── controller
├── dto
├── enums
├── model
├── repository
└── service

---

## 🔍 Fraud Detection Rules

| Rule | Score |
|------|------:|
| Duplicate Invoice | 35 |
| New Vendor High Value | 20 |
| Round Amount | 15 |
| Missing GST | 10 |
| Off Hours Submission | 10 |

---

## ⚠️ Risk Levels

| Score | Risk |
|------:|------|
| 0–29 | LOW |
| 30–59 | MEDIUM |
| 60–100 | HIGH |

---

## 📡 API Endpoints

| Method | Endpoint |
|---------|----------|
| POST | /api/vendors |
| POST | /api/invoices |
| GET | /api/invoices/{id} |
| GET | /api/invoices/{id}/fraud-logs |
| PUT | /api/invoices/{id}/approve |

---

## 💻 Author

**M. Thaher Basha**

GitHub:
https://github.com/M-Thaher-Basha