package com.example.data.model

data class TemplateItem(
    val id: String,
    val title: String,
    val category: String,
    val letterType: LetterType,
    val description: String,
    val promptSuggestion: String,
    val initialLetter: Letter,
    val hindiTitle: String = "",
    val hindiDescription: String = "",
    val hindiCategory: String = "",
    val hindiLetter: Letter? = null
) {
    fun getTitle(languageCode: String): String {
        return if (languageCode.equals("hi", ignoreCase = true) && hindiTitle.isNotBlank()) {
            hindiTitle
        } else {
            title
        }
    }

    fun getDescription(languageCode: String): String {
        return if (languageCode.equals("hi", ignoreCase = true) && hindiDescription.isNotBlank()) {
            hindiDescription
        } else {
            description
        }
    }

    fun getCategory(languageCode: String): String {
        return if (languageCode.equals("hi", ignoreCase = true) && hindiCategory.isNotBlank()) {
            hindiCategory
        } else {
            category
        }
    }

    fun getLetterForLanguage(languageCode: String): Letter {
        return if (languageCode.equals("hi", ignoreCase = true) && hindiLetter != null) {
            hindiLetter
        } else {
            initialLetter
        }
    }

    companion object {
        val ALL: List<TemplateItem> = listOf(
            // SCHOOL: SICK LEAVE
            TemplateItem(
                id = "school_sick_leave",
                title = "Sick Leave Application",
                category = "School",
                letterType = LetterType.SCHOOL_APPLICATION,
                description = "Application to School Principal for 3 days leave due to illness",
                promptSuggestion = "Write an application to my school principal requesting three days leave due to viral fever",
                initialLetter = Letter(
                    title = "Sick Leave Application to Principal",
                    letterType = LetterType.SCHOOL_APPLICATION,
                    tone = LetterTone.RESPECTFUL,
                    letterLanguage = "en",
                    senderDetails = "[STUDENT NAME]\nClass [CLASS] - Section [SECTION]\nRoll No: [ROLL NUMBER]\n[SCHOOL NAME]",
                    recipientDetails = "The Principal,\n[SCHOOL NAME],\n[CITY / LOCATION]",
                    subject = "Application for 3 days sick leave due to viral fever",
                    salutation = "Respected Sir/Madam,",
                    bodyParagraphs = listOf(
                        "With due respect, I beg to state that I have been suffering from high viral fever since yesterday. The doctor has advised me complete bed rest for three days to recover properly.",
                        "Therefore, I kindly request you to grant me leave of absence from [START DATE] to [END DATE]. I assure you that I will cover all missed classwork and homework upon my return.",
                        "I have attached the doctor's prescription for your kind reference."
                    ),
                    complimentaryClose = "Yours obediently,",
                    signerName = "[STUDENT NAME]",
                    signerDesignation = "Class [CLASS], Roll No [ROLL NUMBER]",
                    enclosures = "1. Medical Certificate / Doctor's Prescription"
                ),
                hindiTitle = "बीमारी के कारण अवकाश प्रार्थना पत्र",
                hindiDescription = "तीन दिन के अवकाश हेतु प्रधानाचार्य महोदय को प्रार्थना पत्र",
                hindiCategory = "विद्यालय",
                hindiLetter = Letter(
                    title = "बीमारी के कारण अवकाश प्रार्थना पत्र",
                    letterType = LetterType.SCHOOL_APPLICATION,
                    tone = LetterTone.RESPECTFUL,
                    letterLanguage = "hi",
                    senderDetails = "[विद्यार्थी का नाम]\nकक्षा [कक्षा] - वर्ग [वर्ग]\nअनुक्रमांक (Roll No): [रोल नंबर]\n[विद्यालय का नाम]",
                    recipientDetails = "प्रधानाचार्य महोदय,\n[विद्यालय का नाम],\n[शहर / स्थान]",
                    subject = "तीव्र अस्वस्थता (बुखार) के कारण 3 दिवसीय अवकाश हेतु प्रार्थना पत्र",
                    salutation = "आदरणीय प्रधानाचार्य महोदय,",
                    bodyParagraphs = listOf(
                        "सविनय निवेदन यह है कि मैं कल रात्रि से तीव्र ज्वर (बुखार) से पीड़ित हूँ तथा मेरा स्वास्थ्य अत्यधिक अस्वस्थ है। चिकित्सक ने मुझे स्वास्थ्य लाभ हेतु तीन दिन तक पूर्ण विश्राम करने की सलाह दी है।",
                        "अतः आपसे विनम्र प्रार्थना है कि मुझे [आरंभिक दिनांक] से [अंतिम दिनांक] तक तीन दिन का अवकाश प्रदान करने की महती कृपा करें। इस अवकाश अवधि में छूटा हुआ समस्त अध्ययन कार्य मैं विद्यालय लौटने पर शीघ्र पूर्ण कर लूँगा/लूँगी।",
                        "संलग्नक के रूप में चिकित्सक का परामर्श पत्र प्रस्तुत है।"
                    ),
                    complimentaryClose = "सधन्यवाद,\nआपका आज्ञाकारी शिष्य / शिष्या,",
                    signerName = "[विद्यार्थी का नाम]",
                    signerDesignation = "कक्षा [कक्षा], रोल नं: [रोल नंबर]",
                    enclosures = "1. चिकित्सक का परामर्श प्रमाण पत्र (Medical Certificate)"
                )
            ),

            // SCHOOL: TC REQUEST
            TemplateItem(
                id = "school_tc_request",
                title = "Transfer Certificate (TC) Request",
                category = "School",
                letterType = LetterType.SCHOOL_APPLICATION,
                description = "Request to Principal for School Leaving / Transfer Certificate",
                promptSuggestion = "Request TC from principal because my father is transferred to another city",
                initialLetter = Letter(
                    title = "Application for Transfer Certificate",
                    letterType = LetterType.SCHOOL_APPLICATION,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "en",
                    senderDetails = "[STUDENT / PARENT NAME]\nParent of [STUDENT NAME]\nAdmission No: [ADMISSION NO]\nContact: [PHONE NUMBER]",
                    recipientDetails = "The Principal,\n[SCHOOL NAME],\n[CITY / DISTRICT]",
                    subject = "Request for issuance of Transfer Certificate (TC)",
                    salutation = "Respected Principal,",
                    bodyParagraphs = listOf(
                        "I am writing to formally request a Transfer Certificate for my child, [STUDENT NAME], who is currently studying in Class [CLASS & SECTION] of your esteemed institution.",
                        "Due to my recent official job transfer to [NEW CITY/STATE], our entire family is relocating by the end of this month. Consequently, my child will continue further studies in a school at the new station.",
                        "All school dues, library books, and laboratory clearances have been cleared. Kindly issue the Transfer Certificate along with the Character Certificate at your earliest convenience."
                    ),
                    complimentaryClose = "Yours faithfully,",
                    signerName = "[PARENT / GUARDIAN NAME]",
                    signerDesignation = "Parent of [STUDENT NAME]"
                ),
                hindiTitle = "स्थानांतरण प्रमाण पत्र (TC) हेतु आवेदन",
                hindiDescription = "अन्य शहर स्थानांतरण होने पर स्कूल छोड़ने का प्रमाण पत्र",
                hindiCategory = "विद्यालय",
                hindiLetter = Letter(
                    title = "स्थानांतरण प्रमाण पत्र (TC) हेतु प्रार्थना पत्र",
                    letterType = LetterType.SCHOOL_APPLICATION,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "hi",
                    senderDetails = "[अभिभावक / छात्र का नाम]\nअभिभावक: [विद्यार्थी का नाम]\nप्रवेश क्रमांक (Admission No): [प्रवेश संख्या]\nसंपर्क नंबर: [फोन नंबर]",
                    recipientDetails = "प्रधानाचार्य महोदय,\n[विद्यालय का नाम],\n[शहर / जिला]",
                    subject = "स्थानांतरण प्रमाण पत्र (TC) एवं चरित्र प्रमाण पत्र निर्गत करने के संबंध में",
                    salutation = "आदरणीय प्रधानाचार्य महोदय,",
                    bodyParagraphs = listOf(
                        "सविनय निवेदन यह है कि मेरे पिता/मेरा स्थानांतरण [नए शहर / राज्य का नाम] में हो गया है, जिसके कारण हमारा परिवार इसी माह के अंत तक वहाँ निवास करने जा रहा है।",
                        "अतः मेरी आगे की शिक्षा वहीं के विद्यालय में संपन्न होगी। विद्यालय के समस्त देय शुल्क, पुस्तकालय की पुस्तकें एवं प्रयोगशाला अनापत्ति प्रमाण पत्र पूर्ण कर लिए गए हैं।",
                        "कृपया मुझे स्थानांतरण प्रमाण पत्र (TC) तथा चरित्र प्रमाण पत्र शीघ्र निर्गत करने की महती कृपा करें।"
                    ),
                    complimentaryClose = "सादर धन्यवाद,\nआपका आज्ञाकारी छात्र / अभिभावक,",
                    signerName = "[अभिभावक / विद्यार्थी का नाम]",
                    signerDesignation = "कक्षा [कक्षा]"
                )
            ),

            // BANK: ACCOUNT CLOSURE
            TemplateItem(
                id = "bank_account_closure",
                title = "Bank Account Closure Request",
                category = "Bank",
                letterType = LetterType.BANK_LETTER,
                description = "Letter to Branch Manager for closure of savings bank account",
                promptSuggestion = "Write a letter to bank manager regarding savings account closure and balance refund",
                initialLetter = Letter(
                    title = "Request for Savings Account Closure",
                    letterType = LetterType.BANK_LETTER,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "en",
                    senderDetails = "[ACCOUNT HOLDER NAME]\n[RESIDENTIAL ADDRESS]\nMobile: [MOBILE NUMBER]\nEmail: [EMAIL ADDRESS]",
                    recipientDetails = "The Branch Manager,\n[BANK NAME],\n[BRANCH NAME AND ADDRESS]",
                    subject = "Request to close Savings Bank Account No. [ACCOUNT NUMBER]",
                    salutation = "Dear Sir/Madam,",
                    bodyParagraphs = listOf(
                        "I hold a Savings Bank Account with your branch under Account Number [ACCOUNT NUMBER]. Due to [REASON e.g., relocation to another city / personal reasons], I am unable to operate this account further.",
                        "I request you to kindly close the said account and transfer the remaining balance along with accrued interest to my alternate bank account (A/C No: [OTHER BANK ACCOUNT NO], IFSC: [IFSC CODE], Bank: [OTHER BANK NAME]) or issue a Demand Draft in my name.",
                        "I am surrendering my unused cheque leaves, Debit/ATM card (ending with [LAST 4 DIGITS]), and the passbook with this application."
                    ),
                    complimentaryClose = "Yours sincerely,",
                    signerName = "[ACCOUNT HOLDER NAME]",
                    signerDesignation = "Account Holder",
                    enclosures = "1. Original Passbook\n2. Unused Cheque Leaves\n3. Deactivated Debit Card\n4. Copy of Aadhaar/ID Proof"
                ),
                hindiTitle = "बैंक खाता बंद कराने हेतु प्रार्थना पत्र",
                hindiDescription = "बचत बैंक खाता बंद कराने एवं शेष राशि अंतरण हेतु शाखा प्रबंधक को पत्र",
                hindiCategory = "बैंक",
                hindiLetter = Letter(
                    title = "बचत बैंक खाता बंद कराने हेतु प्रार्थना पत्र",
                    letterType = LetterType.BANK_LETTER,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "hi",
                    senderDetails = "[खाताधारक का नाम]\n[स्थायी पता]\nमोबाइल नंबर: [मोबाइल नंबर]\nईमेल: [ईमेल आईडी]",
                    recipientDetails = "शाखा प्रबंधक महोदय,\n[बैंक का नाम],\n[शाखा का नाम एवं पता]",
                    subject = "बचत बैंक खाता संख्या [खाता संख्या] को स्थायी रूप से बंद करने के संबंध में",
                    salutation = "आदरणीय शाखा प्रबंधक महोदय,",
                    bodyParagraphs = listOf(
                        "सविनय निवेदन यह है कि आपकी शाखा में मेरा बचत बैंक खाता संख्या [खाता संख्या] संचालित है। व्यक्तिगत कारणों एवं अन्य स्थान पर निवास स्थानांतरित होने के कारण मैं इस खाते का आगे संचालन करने में असमर्थ हूँ।",
                        "अतः आपसे विनम्र अनुरोध है कि उक्त खाते को बंद करने की कृपा करें तथा खाते में शेष जमा धनराशि मेरे अन्य बैंक खाते (खाता संख्या: [अन्य खाता संख्या], IFSC कोड: [IFSC कोड], बैंक: [बैंक नाम]) में अंतरित करने या बैंक ड्राफ्ट जारी करने का कष्ट करें।",
                        "मैं अपनी मूल पासबुक, अप्रयुक्त चेक पत्ते एवं एटीएम डेबिट कार्ड इस प्रार्थना पत्र के साथ संलग्न कर रहा/रही हूँ।"
                    ),
                    complimentaryClose = "सादर धन्यवाद,\nभवदीय,",
                    signerName = "[खाताधारक का नाम]",
                    signerDesignation = "खाताधारक",
                    enclosures = "1. मूल पासबुक\n2. अप्रयुक्त चेक पत्ते\n3. एटीएम/डेबिट कार्ड\n4. पहचान पत्र की प्रति (आधार कार्ड)"
                )
            ),

            // BANK: ATM & CHEQUE REISSUE
            TemplateItem(
                id = "bank_cheque_atm_reissue",
                title = "New ATM Card & Cheque Book Request",
                category = "Bank",
                letterType = LetterType.BANK_LETTER,
                description = "Request to Branch Manager for re-issuing Debit Card and Cheque Book",
                promptSuggestion = "Write letter to bank manager to issue new ATM card and cheque book",
                initialLetter = Letter(
                    title = "Application for New ATM Card & Cheque Book",
                    letterType = LetterType.BANK_LETTER,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "en",
                    senderDetails = "[NAME]\nAccount No: [ACCOUNT NUMBER]\nContact: [PHONE NUMBER]",
                    recipientDetails = "The Branch Manager,\n[BANK NAME],\n[BRANCH NAME]",
                    subject = "Application for issue of new ATM Debit Card and Cheque Book",
                    salutation = "Dear Sir/Madam,",
                    bodyParagraphs = listOf(
                        "I maintain Savings Bank Account No. [ACCOUNT NUMBER] at your branch. My previous ATM Debit Card recently expired/was lost, and I have consumed all cheque leaves from my previous cheque book.",
                        "Kindly issue a new EMV chip Debit Card and a 50-leaf personalized cheque book to my registered residential address at the earliest.",
                        "I authorize the bank to deduct the applicable charges from my account balance."
                    ),
                    complimentaryClose = "Yours faithfully,",
                    signerName = "[YOUR NAME]",
                    signerDesignation = "Account Holder"
                ),
                hindiTitle = "नया एटीएम कार्ड एवं चेकबुक जारी कराने हेतु पत्र",
                hindiDescription = "शाखा प्रबंधक को नया डेबिट कार्ड एवं चेकबुक जारी करने का आवेदन",
                hindiCategory = "बैंक",
                hindiLetter = Letter(
                    title = "नया एटीएम कार्ड एवं चेकबुक जारी कराने हेतु प्रार्थना पत्र",
                    letterType = LetterType.BANK_LETTER,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "hi",
                    senderDetails = "[खाताधारक का नाम]\nखाता संख्या: [खाता संख्या]\nसंपर्क: [मोबाइल नंबर]",
                    recipientDetails = "शाखा प्रबंधक महोदय,\n[बैंक का नाम],\n[शाखा का नाम एवं पता]",
                    subject = "नया एटीएम/डेबिट कार्ड तथा चेकबुक जारी करने के संबंध में",
                    salutation = "महोदय / महोदया,",
                    bodyParagraphs = listOf(
                        "सविनय निवेदन है कि आपकी बैंक शाखा में मेरी बचत खाता संख्या [खाता संख्या] संचालित है। मेरा पुराना एटीएम कार्ड खो गया है/अवधि समाप्त हो चुकी है तथा पूर्व चेकबुक के सभी चेक पत्ते समाप्त हो चुके हैं।",
                        "अतः आपसे सादर अनुरोध है कि मेरे खाते पर एक नया ईएमवी चिप डेबिट कार्ड एवं 50 पत्तों की चेकबुक मेरे पंजीकृत पते पर डाक द्वारा भेजने की कृपा करें।",
                        "इसके लिए बैंक द्वारा निर्धारित सेवा शुल्क मेरे खाते से काटने की अनुमति प्रदान करता/करती हूँ।"
                    ),
                    complimentaryClose = "सधन्यवाद,\nभवदीय,",
                    signerName = "[खाताधारक का नाम]",
                    signerDesignation = "खाताधारक"
                )
            ),

            // OFFICE: RESIGNATION
            TemplateItem(
                id = "office_resignation",
                title = "Formal Resignation Letter",
                category = "Office",
                letterType = LetterType.OFFICE_LETTER,
                description = "Standard corporate resignation letter serving full notice period",
                promptSuggestion = "Write a formal resignation letter serving notice period of 30 days",
                initialLetter = Letter(
                    title = "Formal Resignation Letter",
                    letterType = LetterType.OFFICE_LETTER,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "en",
                    senderDetails = "[EMPLOYEE NAME]\n[CURRENT DESIGNATION]\nEmployee ID: [EMPLOYEE ID]\nDepartment: [DEPARTMENT]",
                    recipientDetails = "[MANAGER / HR HEAD NAME],\n[COMPANY NAME],\n[OFFICE ADDRESS]",
                    subject = "Resignation from the position of [CURRENT DESIGNATION]",
                    salutation = "Dear [MANAGER NAME],",
                    bodyParagraphs = listOf(
                        "Please accept this letter as formal notification that I am resigning from my position as [CURRENT DESIGNATION] at [COMPANY NAME]. My last working day with the organization will be [LAST WORKING DATE], in accordance with the standard notice period of [NOTICE PERIOD e.g., 30 days].",
                        "I would like to express my sincere gratitude for the opportunities I have had during my tenure with the company. I have greatly enjoyed collaborating with such a talented team and have grown both professionally and personally.",
                        "During my remaining weeks, I am committed to completing my ongoing responsibilities and assisting in handing over my duties to ensure a seamless transition for the department."
                    ),
                    complimentaryClose = "Sincerely,",
                    signerName = "[EMPLOYEE NAME]",
                    signerDesignation = "[CURRENT DESIGNATION]"
                ),
                hindiTitle = "औपचारिक त्यागपत्र (Resignation Letter)",
                hindiDescription = "कंपनी या संस्थान में नोटिस अवधि के साथ पद से त्यागपत्र",
                hindiCategory = "कार्यालय",
                hindiLetter = Letter(
                    title = "औपचारिक त्यागपत्र (Resignation Letter)",
                    letterType = LetterType.OFFICE_LETTER,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "hi",
                    senderDetails = "[कर्मचारी का नाम]\nपद: [वर्तमान पद]\nकर्मचारी आईडी: [आईडी नंबर]\nविभाग: [विभाग का नाम]",
                    recipientDetails = "विभागाध्यक्ष / एचआर प्रमुख महोदय,\n[कंपनी / संस्थान का नाम],\n[कार्यालय का पता]",
                    subject = "[वर्तमान पद] के पद से औपचारिक त्यागपत्र प्रस्तुत करने के संबंध में",
                    salutation = "आदरणीय महोदय / महोदया,",
                    bodyParagraphs = listOf(
                        "कृपया इस पत्र को [कंपनी का नाम] में मेरे वर्तमान पद [वर्तमान पद] से औपचारिक त्यागपत्र के रूप में स्वीकार करें। नियमानुसार मेरा नोटिस पीरियड [नोटिस अवधि e.g. 30 दिन] रहेगा तथा मेरा अंतिम कार्य दिवस [अंतिम कार्य दिनांक] होगा।",
                        "मैं इस संस्थान में अपने कार्यकाल के दौरान मिले निरंतर मार्गदर्शन, स्नेह एवं सहयोग के लिए हृदय से आभारी हूँ। इस संस्था के साथ कार्य करने का अनुभव मेरे पेशेवर जीवन के लिए अत्यंत गौरवपूर्ण व फलदायी रहा है।",
                        "नोटिस अवधि के दौरान मैं अपनी सभी वर्तमान जिम्मेदारियों को पूर्ण करूँगा/करूँगी तथा नए सहयोगी को कार्यभार सौंपने में पूर्ण सहयोग प्रदान करूँगा/करूँगी।"
                    ),
                    complimentaryClose = "सादर धन्यवाद,\nभवदीय,",
                    signerName = "[कर्मचारी का नाम]",
                    signerDesignation = "[वर्तमान पद]"
                )
            ),

            // OFFICE: LEAVE REQUEST
            TemplateItem(
                id = "office_leave_request",
                title = "Workplace Casual / Urgent Leave",
                category = "Office",
                letterType = LetterType.LEAVE_APPLICATION,
                description = "Leave request to team manager for personal or urgent work",
                promptSuggestion = "Write leave request to my manager for two days urgent family commitment",
                initialLetter = Letter(
                    title = "Application for Urgent Leave",
                    letterType = LetterType.LEAVE_APPLICATION,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "en",
                    senderDetails = "[YOUR NAME]\n[DESIGNATION], [TEAM NAME]\nEmployee ID: [EMP ID]",
                    recipientDetails = "The Reporting Manager,\n[COMPANY NAME],\n[LOCATION]",
                    subject = "Application for 2 days leave of absence on [DATES]",
                    salutation = "Dear [MANAGER NAME],",
                    bodyParagraphs = listOf(
                        "I am writing to formally request leave of absence for two days, on [START DATE] and [END DATE], due to an urgent personal family commitment.",
                        "Before proceeding on leave, I have ensured that all critical deliverable deadlines for this week are met. My colleague [COLLEAGUE NAME] has graciously agreed to cover any critical escalations in my absence.",
                        "I will resume my duties on [RETURN DATE]. Thank you for your understanding."
                    ),
                    complimentaryClose = "Best regards,",
                    signerName = "[YOUR NAME]",
                    signerDesignation = "[DESIGNATION]"
                ),
                hindiTitle = "कार्यालयीन आवश्यक अवकाश प्रार्थना पत्र",
                hindiDescription = "प्रबंधक को 2 दिन के व्यक्तिगत/पारिवारिक कार्य हेतु अवकाश आवेदन",
                hindiCategory = "कार्यालय",
                hindiLetter = Letter(
                    title = "आकस्मिक कार्य अवकाश प्रार्थना पत्र",
                    letterType = LetterType.LEAVE_APPLICATION,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "hi",
                    senderDetails = "[कर्मचारी का नाम]\nपद: [पद का नाम], [विभाग]\nकर्मचारी आईडी: [आईडी नंबर]",
                    recipientDetails = "वरिष्ठ प्रबंधक महोदय,\n[कंपनी / संस्थान का नाम],\n[स्थान / शहर]",
                    subject = "अपरिहार्य पारिवारिक कार्यवश 2 दिवसीय अवकाश की स्वीकृति हेतु",
                    salutation = "आदरणीय महोदय / महोदया,",
                    bodyParagraphs = listOf(
                        "सविनय निवेदन यह है कि मुझे एक अति आवश्यक पारिवारिक कार्य हेतु [आरंभिक दिनांक] से [अंतिम दिनांक] तक दो दिन के अवकाश की आवश्यकता है।",
                        "अवकाश पर जाने से पूर्व मैंने अपने चालू सप्ताह के सभी महत्वपूर्ण प्रोजेक्ट कार्य पूर्ण कर लिए हैं तथा मेरी अनुपस्थिति में सहयोगी [सहयोगी का नाम] आपातकालीन कार्यों का समन्वय करेंगे।",
                        "मैं [कार्य पर वापसी दिनांक] को नियमित रूप से अपने कर्तव्य पर उपस्थित हो जाऊँगा/जाऊँगी। कृपया अवकाश स्वीकृत करने की कृपा करें।"
                    ),
                    complimentaryClose = "सादर धन्यवाद,\nभवदीय,",
                    signerName = "[कर्मचारी का नाम]",
                    signerDesignation = "[पद का नाम]"
                )
            ),

            // JOB APPLICATION
            TemplateItem(
                id = "job_application_cover_letter",
                title = "Job Application & Cover Letter",
                category = "Job",
                letterType = LetterType.JOB_APPLICATION,
                description = "Professional application highlighting skills, background, and enthusiasm",
                promptSuggestion = "Write a job application cover letter for software engineer position",
                initialLetter = Letter(
                    title = "Application for the Position of [JOB TITLE]",
                    letterType = LetterType.JOB_APPLICATION,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "en",
                    senderDetails = "[CANDIDATE NAME]\n[CITY, STATE]\nPhone: [PHONE NUMBER]\nEmail: [EMAIL ADDRESS]\nLinkedIn: [LINKEDIN PROFILE]",
                    recipientDetails = "The Hiring Manager,\n[COMPANY NAME],\n[COMPANY ADDRESS]",
                    subject = "Application for the position of [JOB TITLE] (Job ID: [JOB ID])",
                    salutation = "Dear Hiring Team,",
                    bodyParagraphs = listOf(
                        "I am writing to express my strong enthusiasm for the [JOB TITLE] role at [COMPANY NAME], as advertised on [PLATFORM/PORTAL]. With [NUMBER OF YEARS] years of experience in [RELEVANT FIELD / TECH STACK], I am eager to contribute to your innovative team.",
                        "In my previous role at [PREVIOUS COMPANY], I spearheaded [SIGNIFICANT ACHIEVEMENT OR PROJECT], which resulted in [POSITIVE OUTCOME / EFFICIENCY IMPROVEMENT]. My core competencies in [KEY SKILL 1], [KEY SKILL 2], and collaborative problem-solving align closely with the requirements of this role.",
                        "I would welcome the opportunity to discuss how my skillset and background can support the growth objectives of [COMPANY NAME]. Enclosed is my resume for your consideration."
                    ),
                    complimentaryClose = "Warm regards,",
                    signerName = "[CANDIDATE NAME]",
                    signerDesignation = "Applicant",
                    enclosures = "1. Curriculum Vitae (Resume)\n2. Portfolio / Reference Sheet"
                ),
                hindiTitle = "नौकरी हेतु आवेदन पत्र (Job Application)",
                hindiDescription = "पदों की रिक्ति हेतु कौशल व अनुभव विवरण सहित औपचारिक आवेदन",
                hindiCategory = "रोजगार",
                hindiLetter = Letter(
                    title = "नौकरी हेतु आवेदन पत्र",
                    letterType = LetterType.JOB_APPLICATION,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "hi",
                    senderDetails = "[आवेदक का नाम]\n[स्थायी पता]\nफोन नंबर: [मोबाइल नंबर]\nईमेल: [ईमेल आईडी]",
                    recipientDetails = "मानव संसाधन प्रबंधक महोदय,\n[कंपनी / संस्थान का नाम],\n[कार्यालय का पता]",
                    subject = "[पद का नाम] के रिक्त पद हेतु औपचारिक आवेदन पत्र",
                    salutation = "आदरणीय महोदय / महोदया,",
                    bodyParagraphs = listOf(
                        "सविनय निवेदन यह है कि मुझे [समाचार पत्र / रोजगार पोर्टल] के माध्यम से आपकी प्रतिष्ठित संस्था में [पद का नाम] के रिक्त पद की जानकारी प्राप्त हुई। मैं इस पद हेतु अपना औपचारिक आवेदन सादर प्रस्तुत कर रहा/रही हूँ।",
                        "मेरे पास [संबंधित क्षेत्र] में [वर्षों की संख्या] वर्ष का सफल पेशेवर अनुभव है। पूर्व संस्थान में मैंने [प्रमुख उपलब्धि / परियोजना] का सफलतापूर्वक संचालन किया है, जिससे कार्यकुशलता में उल्लेखनीय वृद्धि हुई।",
                        "मुझे पूर्ण विश्वास है कि मेरी योग्यता एवं कार्यनिष्ठा आपकी संस्था के लक्ष्यों की प्राप्ति में महत्वपूर्ण योगदान देगी। विचारार्थ मेरा बायोडाटा (Resume) संलग्न है।"
                    ),
                    complimentaryClose = "सधन्यवाद,\nभवदीय,",
                    signerName = "[आवेदक का नाम]",
                    signerDesignation = "आवेदक",
                    enclosures = "1. विस्तृत बायोडाटा (Resume)\n2. शैक्षणिक व अनुभव प्रमाण पत्र"
                )
            ),

            // GOVERNMENT: CIVIC GRIEVANCE
            TemplateItem(
                id = "gov_municipal_complaint",
                title = "Civic & Municipal Grievance",
                category = "Government",
                letterType = LetterType.GOVERNMENT_LETTER,
                description = "Official complaint to Municipal Commissioner regarding road and drainage issue",
                promptSuggestion = "Write an application to municipal commissioner regarding bad road condition and street lights",
                initialLetter = Letter(
                    title = "Complaint Regarding Civic Amenities in [LOCALITY]",
                    letterType = LetterType.GOVERNMENT_LETTER,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "en",
                    senderDetails = "[RESIDENT NAME]\nResident of [STREET / WARD NO],\n[COLONY / LOCALITY],\n[CITY] - [PINCODE]",
                    recipientDetails = "The Municipal Commissioner,\n[MUNICIPAL CORPORATION NAME],\n[CIVIC CENTER / OFFICE ADDRESS]",
                    subject = "Urgent grievance regarding broken road surface and defunct streetlights in [LOCALITY]",
                    salutation = "Respected Officer,",
                    bodyParagraphs = listOf(
                        "I wish to bring to your urgent attention the severe deterioration of civic amenities along [STREET NAME / LOCALITY ROAD] in Ward Number [WARD NO].",
                        "The road has developed large potholes over the last two months, creating a severe hazard for two-wheelers, school vans, and elderly pedestrians. Furthermore, several streetlights in this stretch have been non-functional, leading to dark spots and security concerns at night.",
                        "Despite verbal complaints to the local zonal supervisor, no remedial repair work has been initiated. We earnestly request your department to inspect the site and initiate resurfacing and street lighting repairs promptly."
                    ),
                    complimentaryClose = "Yours faithfully,",
                    signerName = "[RESIDENT NAME]",
                    signerDesignation = "Resident / Secretary of Residents Welfare Association"
                ),
                hindiTitle = "नगर निगम जनसमस्या शिकायत पत्र",
                hindiDescription = "सड़क के गड्ढों व बंद स्ट्रीट लाइट की मरम्मत हेतु नगर आयुक्त को पत्र",
                hindiCategory = "जनसमस्या",
                hindiLetter = Letter(
                    title = "सड़क एवं जनसमस्या शिकायत पत्र",
                    letterType = LetterType.GOVERNMENT_LETTER,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "hi",
                    senderDetails = "[शिकायतकर्ता का नाम]\nनिवासी: [वार्ड नंबर / मोहल्ला],\n[कॉलोनी का नाम],\n[शहर / जिला] - [पिनकोड]",
                    recipientDetails = "नगर आयुक्त महोदय,\n[नगर निगम का नाम],\n[कार्यालय का पता एवं शहर]",
                    subject = "[मोहल्ला / वार्ड का नाम] में क्षतिग्रस्त सड़क एवं बंद स्ट्रीट लाइटों के संबंध में",
                    salutation = "आदरणीय नगर आयुक्त महोदय,",
                    bodyParagraphs = listOf(
                        "सविनय निवेदन यह है कि मैं आपका ध्यान हमारे क्षेत्र [मोहल्ला / मार्ग का नाम] की अत्यंत दयनीय नागरिक सुविधाओं की ओर आकृष्ट करना चाहता/चाहती हूँ।",
                        "मुख्य मार्ग पर गहरे गड्ढे बन चुके हैं, जिससे आए दिन दोपहिया वाहन चालक, विद्यालय के बच्चे एवं वृद्धजन दुर्घटनाग्रस्त हो रहे हैं। साथ ही इस मार्ग की स्ट्रीट लाइटें विगत कई सप्ताह से खराब पड़ी हैं, जिससे रात्रि में अंधकार और असुरक्षा का माहौल बना रहता है।",
                        "अतः जनहित में आपसे सादर अनुरोध है कि संबंधित अधिकारियों को तत्काल स्थल निरीक्षण कर सड़क मरम्मत एवं प्रकाश व्यवस्था बहाल कराने का निर्देश देने की कृपा करें।"
                    ),
                    complimentaryClose = "सधन्यवाद,\nभवदीय / समस्त क्षेत्रवासी,",
                    signerName = "[शिकायतकर्ता का नाम]",
                    signerDesignation = "स्थानीय नागरिक"
                )
            ),

            // COMPLAINT: DEFECTIVE PRODUCT
            TemplateItem(
                id = "complaint_defective_product",
                title = "Defective Product & Consumer Claim",
                category = "Complaint",
                letterType = LetterType.COMPLAINT_LETTER,
                description = "Formal notice demanding replacement or full refund for defective item",
                promptSuggestion = "Write complaint letter to customer support for damaged laptop delivered online",
                initialLetter = Letter(
                    title = "Complaint Regarding Defective Product - Order #[ORDER ID]",
                    letterType = LetterType.COMPLAINT_LETTER,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "en",
                    senderDetails = "[CUSTOMER NAME]\n[ADDRESS]\nMobile: [MOBILE NO]\nOrder ID: [ORDER ID]",
                    recipientDetails = "Head of Customer Support,\n[COMPANY / RETAILER NAME],\n[REGISTERED OFFICE ADDRESS]",
                    subject = "Formal complaint and request for immediate refund/replacement for Order #[ORDER ID]",
                    salutation = "Dear Support Manager,",
                    bodyParagraphs = listOf(
                        "I am writing to lodge a formal complaint regarding [PRODUCT NAME], purchased through your portal on [PURCHASE DATE] under Invoice Number [INVOICE NO].",
                        "Upon delivery and unboxing on [DELIVERY DATE], I discovered that the item was received in a physically damaged and defective state [DESCRIBE SPECIFIC DEFECT]. Despite raising support ticket #[TICKET NO], the issue remains unresolved after [X] days.",
                        "As per the Consumer Protection Guidelines, I hereby request either an immediate replacement unit with intact sealing or a 100% refund credited back to the original source of payment within seven working days."
                    ),
                    complimentaryClose = "Yours sincerely,",
                    signerName = "[CUSTOMER NAME]",
                    signerDesignation = "Consumer",
                    enclosures = "1. Tax Invoice Copy\n2. Unboxing Photographs & Defect Evidence"
                ),
                hindiTitle = "दोषपूर्ण उत्पाद वापसी/धनवापसी हेतु शिकायत पत्र",
                hindiDescription = "ऑनलाइन खरीदे गए खराब उत्पाद के बदले नया सामान या रिफंड हेतु पत्र",
                hindiCategory = "शिकायत",
                hindiLetter = Letter(
                    title = "दोषपूर्ण उत्पाद शिकायत पत्र",
                    letterType = LetterType.COMPLAINT_LETTER,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "hi",
                    senderDetails = "[उपभोक्ता का नाम]\n[पता]\nमोबाइल नंबर: [फोन नंबर]\nऑर्डर संख्या: [ऑर्डर आईडी]",
                    recipientDetails = "ग्राहक सेवा प्रमुख महोदय,\n[कंपनी / विक्रेता का नाम],\n[पंजीकृत कार्यालय का पता]",
                    subject = "दोषपूर्ण उत्पाद (ऑर्डर संख्या [ऑर्डर आईडी]) के बदले नया उत्पाद या पूर्ण धनराशि वापसी के संबंध में",
                    salutation = "महोदय / महोदया,",
                    bodyParagraphs = listOf(
                        "सविनय निवेदन यह है कि मैंने दिनांक [क्रय दिनांक] को आपके पोर्टल से चालान संख्या [चालान संख्या] के तहत [उत्पाद का नाम] क्रय किया था।",
                        "दिनांक [वितरण दिनांक] को पार्सल प्राप्त करने पर यह पाया गया कि उत्पाद क्षतिग्रस्त एवं अनुपयोगी अवस्था में डिलीवर हुआ है। ग्राहक सेवा में शिकायत दर्ज कराने के बावजूद अब तक कोई समाधान नहीं हुआ है।",
                        "अतः उपभोक्ता संरक्षण नियमों के अंतर्गत आपसे अनुरोध है कि या तो इस दोषपूर्ण उत्पाद को बदलकर नया उत्पाद प्रदान करें अथवा सात दिवस के भीतर पूर्ण क्रय धनराशि मेरे मूल बैंक खाते में वापस करने की कृपा करें।"
                    ),
                    complimentaryClose = "सधन्यवाद,\nभवदीय,",
                    signerName = "[उपभोक्ता का नाम]",
                    signerDesignation = "उपभोक्ता",
                    enclosures = "1. टैक्स इनवॉइस की प्रति\n2. क्षतिग्रस्त उत्पाद के छायाचित्र (Photos)"
                )
            ),

            // BUSINESS: RFQ
            TemplateItem(
                id = "business_quotation_request",
                title = "Request for Quotation (RFQ)",
                category = "Business",
                letterType = LetterType.BUSINESS_LETTER,
                description = "Commercial inquiry requesting itemized quotation and payment terms",
                promptSuggestion = "Write a formal business letter requesting quotation for office computers and networking",
                initialLetter = Letter(
                    title = "Request for Quotation - [EQUIPMENT / SERVICES]",
                    letterType = LetterType.BUSINESS_LETTER,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "en",
                    senderDetails = "[YOUR NAME]\n[DESIGNATION]\n[YOUR COMPANY NAME]\n[OFFICE ADDRESS]",
                    recipientDetails = "The Sales Director,\n[VENDOR COMPANY NAME],\n[VENDOR ADDRESS]",
                    subject = "Request for itemized price quotation for [ITEMS / SERVICES]",
                    salutation = "Dear Sir/Madam,",
                    bodyParagraphs = listOf(
                        "Our organization is currently procuring [PRODUCTS/SERVICES e.g., 50 desktop workstations and office networking setup] for our upcoming branch office in [CITY].",
                        "We invite your commercial proposal containing detailed itemized pricing, warranty periods, delivery schedule, and standard commercial payment terms for the specifications detailed in Annexure A.",
                        "Please provide your competitive quotation no later than [DEADLINE DATE]. We look forward to evaluating your offer."
                    ),
                    complimentaryClose = "Best regards,",
                    signerName = "[YOUR NAME]",
                    signerDesignation = "[DESIGNATION], Procurement Dept"
                ),
                hindiTitle = "व्यावसायिक कोटेशन (मूल्य सूची) मांग पत्र",
                hindiDescription = "कार्यालयीन सामग्री एवं सेवाओं के व्यावसायिक कोटेशन हेतु पत्र",
                hindiCategory = "व्यापार",
                hindiLetter = Letter(
                    title = "व्यावसायिक कोटेशन मांग पत्र",
                    letterType = LetterType.BUSINESS_LETTER,
                    tone = LetterTone.FORMAL,
                    letterLanguage = "hi",
                    senderDetails = "[आपका नाम]\n[पद]\n[कंपनी / संस्थान का नाम]\n[कार्यालय का पता]",
                    recipientDetails = "बिक्री प्रबंधक महोदय,\n[विक्रेता कंपनी का नाम],\n[कार्यालय का पता]",
                    subject = "[सामग्री / सेवा का नाम] हेतु मदवार मूल्य कोटेशन प्रेषित करने के संबंध में",
                    salutation = "महोदय / महोदया,",
                    bodyParagraphs = listOf(
                        "हमारी संस्था [शहर] स्थित अपने नए कार्यालय हेतु [सामग्री/सेवा e.g., 50 कंप्यूटर सिस्टम व नेटवर्किंग उपकरण] क्रय करने की योजना बना रही है।",
                        "अतः आपसे अनुरोध है कि संलग्न विवरण के अनुसार अपनी न्यूनतम दरें, वारंटी अवधि, आपूर्ति समय-सीमा तथा भुगतान शर्तों सहित औपचारिक व्यावसायिक कोटेशन प्रेषित करने की कृपा करें।",
                        "कृपया अपना कोटेशन दिनांक [अंतिम दिनांक] तक उपलब्ध कराएं ताकि क्रय समिति द्वारा शीघ्र निर्णय लिया जा सके।"
                    ),
                    complimentaryClose = "सादर धन्यवाद,\nभवदीय,",
                    signerName = "[आपका नाम]",
                    signerDesignation = "[पद], क्रय विभाग"
                )
            )
        )

        fun getCategories(languageCode: String = "en"): List<String> {
            val isHindi = languageCode.equals("hi", ignoreCase = true)
            val list = if (isHindi) {
                listOf("सभी (All)") + ALL.map { it.getCategory("hi") }.distinct()
            } else {
                listOf("All") + ALL.map { it.category }.distinct()
            }
            return list
        }
    }
}
