package com.example.data.model

data class TemplateItem(
    val id: String,
    val title: String,
    val category: String,
    val letterType: LetterType,
    val description: String,
    val promptSuggestion: String,
    val initialLetter: Letter
) {
    companion object {
        val ALL: List<TemplateItem> = listOf(
            // SCHOOL
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
                )
            ),
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
                )
            ),

            // BANK
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
                )
            ),
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
                )
            ),

            // OFFICE & WORKPLACE
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
                )
            ),
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
                )
            ),

            // GOVERNMENT & CIVIC
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
                )
            ),

            // COMPLAINT
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
                )
            ),

            // BUSINESS
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
                )
            )
        )

        fun getCategories(): List<String> = listOf("All") + ALL.map { it.category }.distinct()
    }
}
