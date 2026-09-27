package com.example.data.curriculum

import com.example.data.model.Chapter
import com.example.data.model.DefinitionItem
import com.example.data.model.DiagramItem
import com.example.data.model.FormulaItem
import com.example.data.model.MockTestInfo
import com.example.data.model.Question
import com.example.data.model.StudyQuestionItem
import com.example.data.model.SubjectInfo

object CurriculumData {

    val SUBJECTS = listOf(
        SubjectInfo(
            id = "math",
            name = "Mathematics",
            hindiName = "गणित",
            description = "Algebra, Geometry, Trigonometry, Calculus, Statistics",
            iconName = "Calculate",
            primaryColorHex = 0xFF2563EB,
            totalChapters = 15
        ),
        SubjectInfo(
            id = "physics",
            name = "Physics",
            hindiName = "भौतिक विज्ञान",
            description = "Mechanics, Electricity, Optics, Waves, Thermodynamics",
            iconName = "Bolt",
            primaryColorHex = 0xFF0284C7,
            totalChapters = 14
        ),
        SubjectInfo(
            id = "chemistry",
            name = "Chemistry",
            hindiName = "रसायन विज्ञान",
            description = "Chemical Reactions, Organic Chemistry, Periodic Table",
            iconName = "Science",
            primaryColorHex = 0xFF7C3AED,
            totalChapters = 14
        ),
        SubjectInfo(
            id = "biology",
            name = "Biology",
            hindiName = "जीव विज्ञान",
            description = "Life Processes, Genetics, Ecology, Reproduction",
            iconName = "Eco",
            primaryColorHex = 0xFF059669,
            totalChapters = 16
        ),
        SubjectInfo(
            id = "english",
            name = "English",
            hindiName = "अंग्रेजी",
            description = "Grammar, Literature, Reading Comprehension, Writing",
            iconName = "MenuBook",
            primaryColorHex = 0xFFD97706,
            totalChapters = 12
        ),
        SubjectInfo(
            id = "hindi",
            name = "Hindi",
            hindiName = "हिन्दी",
            description = "व्याकरण, क्षितिज, कृतिका, अपठित गद्यांश व लेखन",
            iconName = "Translate",
            primaryColorHex = 0xFFE11D48,
            totalChapters = 12
        ),
        SubjectInfo(
            id = "social",
            name = "Social Science",
            hindiName = "सामाजिक विज्ञान",
            description = "History, Geography, Political Science, Economics",
            iconName = "Public",
            primaryColorHex = 0xFFB45309,
            totalChapters = 18
        ),
        SubjectInfo(
            id = "computer",
            name = "Computer Science",
            hindiName = "कंप्यूटर विज्ञान",
            description = "Python, Data Structures, SQL, Networking, Cyber Ethics",
            iconName = "Terminal",
            primaryColorHex = 0xFF4F46E5,
            totalChapters = 10
        )
    )

    fun getSubjectsForClass(classNum: Int): List<SubjectInfo> {
        // Classes 6-8 have integrated Science, but for Edu Card all 8 core disciplines are available
        return SUBJECTS
    }

    fun getChapters(classNum: Int, subjectId: String): List<Chapter> {
        val key = "${classNum}_${subjectId}"
        return CHAPTER_DATABASE[key] ?: generateGenericChapters(classNum, subjectId)
    }

    private val CHAPTER_DATABASE: Map<String, List<Chapter>> = mapOf(
        // Class 10 Physics
        "10_physics" to listOf(
            Chapter(
                id = "10_phys_light",
                classNum = 10,
                subjectId = "physics",
                chapterNum = 1,
                title = "Light: Reflection and Refraction",
                overview = "Fundamental optical phenomena: laws of reflection, spherical mirrors, refraction through glass slab and lenses, power of lens.",
                notes = """
                    Light is a form of electromagnetic radiation that causes the sensation of vision. 
                    
                    1. Reflection of Light:
                    Bouncing back of light rays into the same medium when falling on a polished surface.
                    - Angle of incidence equals angle of reflection (∠i = ∠r).
                    - Incident ray, reflected ray, and the normal at the point of incidence all lie in the same plane.
                    
                    2. Spherical Mirrors:
                    - Concave Mirror: Converging mirror, forms real and virtual images.
                    - Convex Mirror: Diverging mirror, always forms virtual, erect, and diminished images. Wide field of view (used as rear-view mirrors).
                    
                    3. Refraction of Light:
                    Bending of light as it passes from one optical medium to another due to change in speed.
                    - Snell's Law: sin(i) / sin(r) = constant (refractive index n).
                    - Optical Density: High refractive index means denser medium where light travels slower.
                    
                    4. Power of Lens:
                    Reciprocal of focal length in meters. Unit is Dioptre (D). Convex lens has +ve power, concave has -ve power.
                """.trimIndent(),
                definitions = listOf(
                    DefinitionItem("Focal Length (f)", "The distance between the pole (or optical centre) and the principal focus of a spherical mirror or lens."),
                    DefinitionItem("Refractive Index (n)", "The ratio of the speed of light in vacuum (c) to the speed of light in a given medium (v): n = c / v."),
                    DefinitionItem("Power of Lens (P)", "The degree of convergence or divergence of light rays achieved by a lens, expressed as P = 1 / f (in meters). Unit: Dioptre (D)."),
                    DefinitionItem("Total Internal Reflection", "The phenomenon occurring when light travels from a denser to a rarer medium at an angle of incidence greater than the critical angle.")
                ),
                formulas = listOf(
                    FormulaItem("Mirror Formula", "1/v + 1/u = 1/f", "Relates object distance (u), image distance (v), and focal length (f)"),
                    FormulaItem("Mirror Magnification", "m = -v / u = h_i / h_o", "Ratio of image height to object height"),
                    FormulaItem("Snell's Law", "n₁·sin(θ₁) = n₂·sin(θ₂)", "Relates refractive indices and angles across boundary"),
                    FormulaItem("Lens Formula", "1/v - 1/u = 1/f", "Sign convention: u is negative for real objects"),
                    FormulaItem("Lens Power", "P = 1 / f (in meters)", "Measured in Dioptres (1 D = 1 m⁻¹)")
                ),
                keyPoints = listOf(
                    "Convex mirrors always produce virtual, erect, and diminished images regardless of object position.",
                    "Concave mirrors produce a magnified virtual image ONLY when the object is between pole (P) and focus (F).",
                    "A convex lens is thicker at the centre and converges parallel rays.",
                    "Sign convention: All distances measured in the direction of incident light are positive; opposite are negative."
                ),
                diagrams = listOf(
                    DiagramItem(
                        title = "Ray Diagram: Concave Mirror (Object at C)",
                        description = "When object is at Centre of Curvature (C), image is formed at C, real, inverted, and same size.",
                        asciiArtOrLabel = "[Mirror Vertex] <--- f ---> [Focus F] <--- f ---> [Center C (Object & Image)]"
                    ),
                    DiagramItem(
                        title = "Refraction through Rectangular Glass Slab",
                        description = "Light ray bends towards normal upon entering glass, bends away on exiting. Emergent ray is parallel to incident ray with lateral displacement.",
                        asciiArtOrLabel = "Air (n1) --> / Normal | i \\ --> Glass (n2) --> \\ Normal | e / --> Air (n1)"
                    )
                ),
                summary = "Reflection involves bouncing off reflective boundaries following i = r. Refraction involves speed variation governed by Snell's Law. Spherical mirrors follow 1/v + 1/u = 1/f while lenses follow 1/v - 1/u = 1/f.",
                importantQuestions = listOf(
                    StudyQuestionItem("Why are convex mirrors preferred as rear-view mirrors in vehicles?", "Convex mirrors always give an erect, though diminished image, and have a much wider field of view as they are curved outwards.", 3),
                    StudyQuestionItem("A concave mirror produces three times magnified real image of an object placed at 10 cm in front of it. Where is the image located?", "Given m = -3, u = -10 cm. Since m = -v/u, -3 = -v / (-10) => v = -30 cm. The image is 30 cm in front of the mirror.", 4),
                    StudyQuestionItem("Define 1 Dioptre of power of a lens.", "One Dioptre is the power of a lens of focal length 1 meter (P = 1/f = 1/1 = 1 D).", 2)
                )
            ),
            Chapter(
                id = "10_phys_electricity",
                classNum = 10,
                subjectId = "physics",
                chapterNum = 2,
                title = "Electricity",
                overview = "Electric current, potential difference, Ohm's law, resistance, series and parallel circuits, heating effect of electric current.",
                notes = """
                    Electricity powers modern civilization through the movement of electric charge.
                    
                    1. Electric Current (I):
                    The rate of flow of electric charges through a cross-section: I = Q / t. Measured in Amperes (A).
                    
                    2. Electric Potential & Potential Difference (V):
                    Work done per unit charge in moving it between two points: V = W / Q. Measured in Volts (V).
                    
                    3. Ohm's Law:
                    At constant temperature, current flowing through a conductor is directly proportional to the potential difference across its ends: V = I · R.
                    
                    4. Factors affecting Resistance:
                    - Directly proportional to length (l).
                    - Inversely proportional to cross-sectional area (A).
                    - Depends on nature of material (resistivity ρ) and temperature: R = ρ · (l / A).
                    
                    5. Resistors in Series & Parallel:
                    - Series: R_eq = R₁ + R₂ + ... + Rₙ (Current is identical, voltages divide).
                    - Parallel: 1/R_eq = 1/R₁ + 1/R₂ + ... + 1/Rₙ (Voltage is identical, currents divide).
                    
                    6. Joule's Law of Heating:
                    Heat produced H = I² · R · t = V · I · t = (V² / R) · t.
                """.trimIndent(),
                definitions = listOf(
                    DefinitionItem("1 Ampere", "Flow of one Coulomb of electric charge per second through any cross section of a conductor (1 A = 1 C/s)."),
                    DefinitionItem("Resistivity (ρ)", "The intrinsic electrical resistance of a conductor of unit length and unit cross-sectional area. SI unit: Ohm-meter (Ω·m)."),
                    DefinitionItem("Electric Power (P)", "The rate at which electrical energy is consumed or dissipated in a circuit: P = V · I. Unit: Watt (W).")
                ),
                formulas = listOf(
                    FormulaItem("Current", "I = Q / t", "I in Amperes, Q in Coulombs, t in seconds"),
                    FormulaItem("Ohm's Law", "V = I · R", "V in Volts, I in Amperes, R in Ohms"),
                    FormulaItem("Resistance", "R = ρ · (l / A)", "ρ is resistivity, l is length, A is area"),
                    FormulaItem("Series Equivalent", "R_s = R₁ + R₂ + R₃", "Total resistance increases"),
                    FormulaItem("Parallel Equivalent", "1/R_p = 1/R₁ + 1/R₂", "Total resistance is lower than smallest branch"),
                    FormulaItem("Joule's Heating", "H = I² · R · t", "Thermal energy in Joules")
                ),
                keyPoints = listOf(
                    "Ammeter has very low resistance and is always connected in series.",
                    "Voltmeter has very high resistance and is always connected in parallel.",
                    "Commercial unit of energy is kilowatt-hour (1 kWh = 3.6 × 10⁶ J = 1 unit).",
                    "Fuse wire is an alloy with high resistance and low melting point for safety."
                ),
                diagrams = listOf(
                    DiagramItem(
                        title = "Series vs Parallel Combination",
                        description = "In series, single path for current; in parallel, multiple branching paths with constant potential difference.",
                        asciiArtOrLabel = "Series: (+)--[R1]--[R2]--(-) | Parallel: (+)--+--[R1]--+--(-)\n                               +--[R2]--+"
                    )
                ),
                summary = "Electric charge flow I = Q/t driven by potential difference V = W/Q governed by Ohm's Law V = IR. Equivalent resistance adds in series and reciprocates in parallel.",
                importantQuestions = listOf(
                    StudyQuestionItem("State Ohm's Law and draw V-I graph for an ohmic conductor.", "Ohm's law states that current through a conductor between two points is directly proportional to voltage across the points at constant temperature. The V-I graph is a straight line passing through the origin.", 3),
                    StudyQuestionItem("Why is parallel arrangement used in domestic electrical circuits?", "1. Each appliance receives full voltage. 2. Appliances can be operated independently. 3. If one fails, others continue working.", 3)
                )
            )
        ),
        // Class 10 Mathematics
        "10_math" to listOf(
            Chapter(
                id = "10_math_real_numbers",
                classNum = 10,
                subjectId = "math",
                chapterNum = 1,
                title = "Real Numbers",
                overview = "Fundamental Theorem of Arithmetic, irrationality proofs, HCF and LCM relationships, decimal expansions.",
                notes = """
                    1. Fundamental Theorem of Arithmetic:
                    Every composite number can be expressed (factorised) as a product of primes uniquely, apart from the order of factors.
                    
                    2. Relationship between HCF and LCM:
                    For any two positive integers a and b:
                    HCF(a, b) × LCM(a, b) = a × b.
                    
                    3. Proof of Irrationality:
                    Proof by contradiction is used to demonstrate numbers like √2, √3, √5, 2 + 3√5 are irrational.
                    Assume p/q is in simplest co-prime form, leading to a common factor contradiction.
                """.trimIndent(),
                definitions = listOf(
                    DefinitionItem("Prime Number", "An integer greater than 1 that has exactly two distinct positive divisors: 1 and itself."),
                    DefinitionItem("Composite Number", "A positive integer greater than 1 that has more than two distinct positive factors."),
                    DefinitionItem("Irrational Number", "A real number that cannot be expressed as a ratio p/q of two integers, having non-terminating and non-repeating decimal expansion.")
                ),
                formulas = listOf(
                    FormulaItem("HCF × LCM Rule", "HCF(a, b) × LCM(a, b) = a × b", "Applies exclusively to two numbers"),
                    FormulaItem("Prime Factorisation", "n = p₁^a · p₂^b · p₃^c", "Unique canonical representation")
                ),
                keyPoints = listOf(
                    "HCF is the product of the smallest power of each common prime factor.",
                    "LCM is the product of the greatest power of each prime factor involved.",
                    "Sum or difference of a rational and irrational number is always irrational.",
                    "Product or quotient of a non-zero rational and irrational number is irrational."
                ),
                diagrams = listOf(
                    DiagramItem(
                        title = "Factor Tree Method",
                        description = "Decomposing 72: 72 = 2 × 36 -> 36 = 2 × 18 -> 18 = 2 × 9 -> 9 = 3 × 3 => 2³ × 3²",
                        asciiArtOrLabel = "72\n ├── 2\n └── 36\n      ├── 2\n      └── 18\n           ├── 2\n           └── 9 (3 × 3)"
                    )
                ),
                summary = "Real numbers encompass rationals and irrationals. Prime factorisation uniquely classifies numbers. HCF × LCM = a × b.",
                importantQuestions = listOf(
                    StudyQuestionItem("Prove that √5 is an irrational number.", "Assume √5 = a/b where a, b are coprime. 5b² = a² => 5 divides a. Let a = 5c => 5b² = 25c² => b² = 5c² => 5 divides b. Hence 5 divides both a and b, contradicting that they are coprime. Thus √5 is irrational.", 4)
                )
            ),
            Chapter(
                id = "10_math_quadratics",
                classNum = 10,
                subjectId = "math",
                chapterNum = 2,
                title = "Quadratic Equations",
                overview = "Standard quadratic form ax² + bx + c = 0, factorisation method, quadratic formula, nature of roots.",
                notes = """
                    A quadratic equation in variable x is an equation of the form ax² + bx + c = 0, where a ≠ 0.
                    
                    Methods of Solving:
                    1. Factorisation (splitting middle term).
                    2. Quadratic Formula (Sridharacharya's Rule):
                       x = [-b ± √(b² - 4ac)] / (2a)
                    
                    Nature of Roots (Discriminant D = b² - 4ac):
                    - If D > 0: Two distinct real roots.
                    - If D = 0: Two equal real roots (x = -b / 2a).
                    - If D < 0: No real roots (complex roots).
                """.trimIndent(),
                definitions = listOf(
                    DefinitionItem("Discriminant (D)", "The expression D = b² - 4ac that determines the nature of the roots of ax² + bx + c = 0."),
                    DefinitionItem("Root / Zero", "A value α of x such that aα² + bα + c = 0.")
                ),
                formulas = listOf(
                    FormulaItem("Quadratic Formula", "x = (-b ± √D) / 2a", "Where D = b² - 4ac"),
                    FormulaItem("Sum of Roots", "α + β = -b / a", "Vieta's formula for degree 2"),
                    FormulaItem("Product of Roots", "α · β = c / a", "Vieta's formula for degree 2")
                ),
                keyPoints = listOf(
                    "A quadratic equation can have at most two real roots.",
                    "If roots are α and β, equation is x² - (α + β)x + (αβ) = 0.",
                    "If D is a perfect square and a,b,c rational, roots are rational."
                ),
                diagrams = listOf(
                    DiagramItem(
                        title = "Parabola and Discriminant Cases",
                        description = "D > 0 cuts x-axis twice; D = 0 touches x-axis at vertex; D < 0 does not intersect x-axis.",
                        asciiArtOrLabel = "D>0: \\_./ (2 roots) | D=0: \\_|_/ (1 root) | D<0: \\__/ above x-axis"
                    )
                ),
                summary = "Quadratic polynomial ax² + bx + c has roots given by (-b ± √D)/(2a). The discriminant D = b² - 4ac characterizes the nature of solutions.",
                importantQuestions = listOf(
                    StudyQuestionItem("Find roots of 2x² - 7x + 3 = 0 using the quadratic formula.", "Here a = 2, b = -7, c = 3. D = (-7)² - 4(2)(3) = 49 - 24 = 25. x = [7 ± √25] / 4 = [7 ± 5] / 4. Hence x = 3 or x = 1/2.", 3)
                )
            )
        ),
        // Class 10 Chemistry
        "10_chemistry" to listOf(
            Chapter(
                id = "10_chem_reactions",
                classNum = 10,
                subjectId = "chemistry",
                chapterNum = 1,
                title = "Chemical Reactions and Equations",
                overview = "Chemical changes, balancing chemical equations, types of reactions, oxidation, reduction, corrosion, and rancidity.",
                notes = """
                    A chemical reaction involves breaking old chemical bonds and forming new bonds to create new substances with different properties.
                    
                    1. Types of Chemical Reactions:
                    - Combination: A + B -> AB (e.g., CaO + H₂O -> Ca(OH)₂ + heat)
                    - Decomposition: AB -> A + B (Thermal, Electrolytic, Photolytic)
                    - Displacement: More reactive metal replaces less reactive metal (Fe + CuSO₄ -> FeSO₄ + Cu)
                    - Double Displacement: Exchange of ions forming a precipitate (Na₂SO₄ + BaCl₂ -> BaSO₄↓ + 2NaCl)
                    - Redox: Simultaneous Oxidation (gain of oxygen / loss of electrons) and Reduction (loss of oxygen / gain of electrons).
                    
                    2. Effects of Oxidation in Daily Life:
                    - Corrosion: Slow degradation of metals (e.g. rusting of iron 4Fe + 3O₂ + xH₂O -> 2Fe₂O₃·xH₂O).
                    - Rancidity: Oxidation of fats and oils resulting in foul smell and taste (prevented by antioxidants, nitrogen packaging).
                """.trimIndent(),
                definitions = listOf(
                    DefinitionItem("Oxidation", "Addition of oxygen or electronegative element, or removal of hydrogen / electrons from a substance."),
                    DefinitionItem("Precipitation Reaction", "A chemical reaction in which two soluble salts in aqueous solution combine to form an insoluble solid (precipitate)."),
                    DefinitionItem("Rancidity", "Aerial oxidation of unsaturated fats and oils leading to unpleasant odour and flavor.")
                ),
                formulas = listOf(
                    FormulaItem("Slaking of Lime", "CaO + H₂O → Ca(OH)₂ + Heat", "Exothermic combination reaction"),
                    FormulaItem("Thermal Decomposition", "2FeSO₄(s) → Fe₂O₃(s) + SO₂(g) + SO₃(g)", "Green crystals turn reddish-brown"),
                    FormulaItem("Rusting Formula", "4Fe + 3O₂ + 2xH₂O → 2Fe₂O₃·xH₂O", "Hydrated iron(III) oxide")
                ),
                keyPoints = listOf(
                    "Exothermic reactions release energy; endothermic reactions absorb heat.",
                    "Respiration is an exothermic process (C₆H₁₂O₆ + 6O₂ -> 6CO₂ + 6H₂O + ATP).",
                    "Photosynthesis is an endothermic process requiring light energy."
                ),
                diagrams = listOf(
                    DiagramItem(
                        title = "Electrolysis of Water",
                        description = "Water decomposes into Hydrogen and Oxygen in a 2:1 volume ratio at cathode and anode respectively.",
                        asciiArtOrLabel = "(-) Cathode: 2H⁺ + 2e⁻ -> H₂ (2 volumes) | (+) Anode: 2O²⁻ -> O₂ + 4e⁻ (1 volume)"
                    )
                ),
                summary = "Reactions rearrange atoms while conserving mass. Five main types: combination, decomposition, displacement, double displacement, and redox.",
                importantQuestions = listOf(
                    StudyQuestionItem("Why is respiration considered an exothermic reaction?", "During digestion, food is broken down into glucose. Glucose combines with oxygen in the cells of our body and provides energy: C₆H₁₂O₆ + 6O₂ → 6CO₂ + 6H₂O + Energy.", 3)
                )
            )
        )
    )

    private fun generateGenericChapters(classNum: Int, subjectId: String): List<Chapter> {
        val subject = SUBJECTS.find { it.id == subjectId }?.name ?: subjectId.replaceFirstChar { it.uppercase() }
        return (1..5).map { idx ->
            Chapter(
                id = "${classNum}_${subjectId}_ch${idx}",
                classNum = classNum,
                subjectId = subjectId,
                chapterNum = idx,
                title = "Chapter $idx: Core $subject Concepts & Principles",
                overview = "Comprehensive syllabus curriculum for Class $classNum $subject covering key conceptual foundations, solved illustrations, and practical exercises.",
                notes = """
                    Welcome to Chapter $idx of Class $classNum $subject.
                    
                    Key Learning Objectives:
                    1. Conceptual foundation and fundamental definitions.
                    2. Analytical derivations and mathematical/logical formulations.
                    3. Practical real-world applications and exam problem-solving strategies.
                    
                    Core Theoretical Framework:
                    Mastering this topic requires understanding the core axioms, identifying pattern similarities in past year question papers, and applying systematic methods.
                """.trimIndent(),
                definitions = listOf(
                    DefinitionItem("Key Principle $idx", "Standard certified academic definition approved for Class $classNum $subject."),
                    DefinitionItem("Operational Law $idx", "Fundamental governing relation observed in empirical study.")
                ),
                formulas = listOf(
                    FormulaItem("Primary Formula $idx", "Y = f(X) + k", "Core relationship for Class $classNum $subject Chapter $idx")
                ),
                keyPoints = listOf(
                    "Always verify units and dimensions before concluding computations.",
                    "Focus on conceptual understanding rather than rote memorization.",
                    "Review past board / exam questions on this chapter regularly."
                ),
                diagrams = listOf(
                    DiagramItem(
                        title = "Concept Schematic: Chapter $idx",
                        description = "Structural flow of concepts from hypothesis to experimental validation.",
                        asciiArtOrLabel = "[Input/Premise] ---> [Transformative Process] ---> [Result/Observation]"
                    )
                ),
                summary = "Chapter $idx provides the essential baseline for Class $classNum $subject, enabling confident mastery in objective and subjective tests.",
                importantQuestions = listOf(
                    StudyQuestionItem("Explain the main significance of concepts in Chapter $idx for Class $classNum.", "Demonstrates the bridge between elementary observations and rigorous analytical evaluation.", 3)
                )
            )
        }
    }

    // Rich Question Bank
    val QUESTIONS: List<Question> = listOf(
        Question(
            id = "q_10_phys_01",
            chapterId = "10_phys_light",
            classNum = 10,
            subjectId = "physics",
            questionText = "Which type of mirror is used as a rear-view mirror in vehicles to provide a wider field of view?",
            optionA = "Plane mirror",
            optionB = "Concave mirror",
            optionC = "Convex mirror",
            optionD = "Cylindrical mirror",
            correctIndex = 2,
            explanation = "Convex mirrors are curved outwards, which allows them to produce virtual, erect, and diminished images, providing drivers with a much wider field of view.",
            difficulty = "Easy",
            category = "MCQ"
        ),
        Question(
            id = "q_10_phys_02",
            chapterId = "10_phys_light",
            classNum = 10,
            subjectId = "physics",
            questionText = "A student places an object 20 cm in front of a concave mirror of focal length 10 cm. What is the nature and position of the image?",
            optionA = "Real, inverted, at 20 cm (at C)",
            optionB = "Virtual, erect, at 10 cm",
            optionC = "Real, inverted, at infinity",
            optionD = "Virtual, erect, behind mirror",
            correctIndex = 0,
            explanation = "Here f = -10 cm, so radius of curvature R = 2f = 20 cm. The object is placed at C (20 cm), so the image is also formed at C (20 cm), real, inverted, and of the same size.",
            difficulty = "Medium",
            category = "PYQ"
        ),
        Question(
            id = "q_10_phys_03",
            chapterId = "10_phys_light",
            classNum = 10,
            subjectId = "physics",
            questionText = "Assertion (A): The power of a convex lens is positive, while that of a concave lens is negative.\nReason (R): Focal length of a convex lens is taken as positive and that of a concave lens as negative.",
            optionA = "Both A and R are true and R is the correct explanation of A",
            optionB = "Both A and R are true but R is NOT the correct explanation of A",
            optionC = "A is true but R is false",
            optionD = "A is false but R is true",
            correctIndex = 0,
            explanation = "Since Power P = 1/f, convex lenses have positive focal lengths so P > 0, while concave lenses have negative focal lengths so P < 0. Both assertion and reason are true and R explains A.",
            difficulty = "Hard",
            category = "ASSERTION_REASON"
        ),
        Question(
            id = "q_10_phys_04",
            chapterId = "10_phys_electricity",
            classNum = 10,
            subjectId = "physics",
            questionText = "Three resistors of 2 Ω, 3 Ω, and 6 Ω are connected in parallel. What is their equivalent resistance?",
            optionA = "11 Ω",
            optionB = "1 Ω",
            optionC = "0.5 Ω",
            optionD = "2.5 Ω",
            correctIndex = 1,
            explanation = "1/R_p = 1/2 + 1/3 + 1/6 = (3 + 2 + 1) / 6 = 6/6 = 1. Therefore, R_p = 1 Ω.",
            difficulty = "Easy",
            category = "MCQ"
        ),
        Question(
            id = "q_10_phys_05",
            chapterId = "10_phys_electricity",
            classNum = 10,
            subjectId = "physics",
            questionText = "A cylindrical wire of length L and cross-sectional area A has resistance R. If it is stretched to double its length keeping volume constant, what is the new resistance?",
            optionA = "2R",
            optionB = "R / 2",
            optionC = "4R",
            optionD = "16R",
            correctIndex = 2,
            explanation = "Since volume V = A·L is constant, doubling length (L' = 2L) halves cross-sectional area (A' = A/2). New resistance R' = ρ(2L)/(A/2) = 4 · ρ(L/A) = 4R.",
            difficulty = "Hard",
            category = "HOTS"
        ),
        Question(
            id = "q_10_math_01",
            chapterId = "10_math_real_numbers",
            classNum = 10,
            subjectId = "math",
            questionText = "If two positive integers a and b are written as a = x³y² and b = xy³, where x, y are prime numbers, then HCF(a, b) is:",
            optionA = "xy",
            optionB = "xy²",
            optionC = "x³y³",
            optionD = "x²y²",
            correctIndex = 1,
            explanation = "HCF is the product of the lowest powers of each common prime factor. Common factors are x and y. Minimum power of x is 1; minimum power of y is 2. So HCF = xy².",
            difficulty = "Medium",
            category = "PYQ"
        ),
        Question(
            id = "q_10_math_02",
            chapterId = "10_math_quadratics",
            classNum = 10,
            subjectId = "math",
            questionText = "For what value of k does the quadratic equation 2x² + kx + 3 = 0 have two equal real roots?",
            optionA = "±√6",
            optionB = "±2√6",
            optionC = "±4",
            optionD = "±12",
            correctIndex = 1,
            explanation = "For equal real roots, Discriminant D = b² - 4ac = 0. Here a = 2, b = k, c = 3. k² - 4(2)(3) = 0 => k² - 24 = 0 => k² = 24 => k = ±√24 = ±2√6.",
            difficulty = "Medium",
            category = "MCQ"
        ),
        Question(
            id = "q_10_chem_01",
            chapterId = "10_chem_reactions",
            classNum = 10,
            subjectId = "chemistry",
            questionText = "When iron nails are dipped in copper sulphate solution for 30 minutes, what observable change takes place?",
            optionA = "Solution turns green and brown deposit forms on nails",
            optionB = "Solution remains blue and gas is evolved",
            optionC = "Solution turns yellow and silver deposit forms",
            optionD = "No reaction occurs",
            correctIndex = 0,
            explanation = "Fe + CuSO₄ (blue) → FeSO₄ (light green) + Cu (reddish-brown). Iron is more reactive than copper and displaces it from copper sulphate solution.",
            difficulty = "Easy",
            category = "CASE_BASED"
        ),
        Question(
            id = "q_10_chem_02",
            chapterId = "10_chem_reactions",
            classNum = 10,
            subjectId = "chemistry",
            questionText = "In the reaction: CuO + H₂ → Cu + H₂O, which substance acts as the reducing agent?",
            optionA = "CuO",
            optionB = "H₂",
            optionC = "Cu",
            optionD = "H₂O",
            correctIndex = 1,
            explanation = "Hydrogen (H₂) gains oxygen to form H₂O and gets oxidized. The substance that undergoes oxidation acts as the reducing agent.",
            difficulty = "Medium",
            category = "OLYMPIAD"
        ),
        Question(
            id = "q_10_bio_01",
            chapterId = "10_bio_life_processes",
            classNum = 10,
            subjectId = "biology",
            questionText = "Which cellular organelle is known as the site of aerobic cellular respiration generating ATP?",
            optionA = "Chloroplast",
            optionB = "Ribosome",
            optionC = "Mitochondria",
            optionD = "Endoplasmic Reticulum",
            correctIndex = 2,
            explanation = "Mitochondria are the powerhouse of the cell where the Krebs cycle and oxidative phosphorylation yield the vast majority of cellular ATP.",
            difficulty = "Easy",
            category = "MCQ"
        ),
        Question(
            id = "q_10_cs_01",
            chapterId = "10_cs_python",
            classNum = 10,
            subjectId = "computer",
            questionText = "In Python, what is the output of the expression `type([1, 2, 3])`?",
            optionA = "<class 'tuple'>",
            optionB = "<class 'set'>",
            optionC = "<class 'list'>",
            optionD = "<class 'dict'>",
            correctIndex = 2,
            explanation = "Square brackets `[...]` denote a mutable sequence list in Python, so its type is `<class 'list'>`.",
            difficulty = "Easy",
            category = "MCQ"
        )
    )

    // Preconfigured Mock Tests
    val MOCK_TESTS: List<MockTestInfo> = listOf(
        MockTestInfo(
            id = "test_class10_boards_01",
            title = "Class 10 CBSE Board Model Test: Science",
            classNum = 10,
            subjectId = "physics",
            durationMinutes = 15,
            totalQuestions = 6,
            isCompetitive = false,
            examType = "Board Exam",
            questions = QUESTIONS.filter { it.classNum == 10 && (it.subjectId == "physics" || it.subjectId == "chemistry" || it.subjectId == "biology") }
        ),
        MockTestInfo(
            id = "test_class10_math_01",
            title = "Class 10 Standard Mathematics Full Quiz",
            classNum = 10,
            subjectId = "math",
            durationMinutes = 20,
            totalQuestions = 5,
            isCompetitive = false,
            examType = "Board Exam",
            questions = QUESTIONS.filter { it.subjectId == "math" }
        ),
        MockTestInfo(
            id = "test_jee_foundation_01",
            title = "JEE / NEET Foundation Challenger Test",
            classNum = 10,
            subjectId = "physics",
            durationMinutes = 25,
            totalQuestions = 8,
            isCompetitive = true,
            examType = "JEE Main",
            questions = QUESTIONS.filter { it.difficulty == "Medium" || it.difficulty == "Hard" }
        ),
        MockTestInfo(
            id = "test_olympiad_01",
            title = "National Science Olympiad (NSO) Mock Test",
            classNum = 10,
            subjectId = "chemistry",
            durationMinutes = 30,
            totalQuestions = 10,
            isCompetitive = true,
            examType = "Olympiad",
            questions = QUESTIONS
        )
    )

    val DEFAULT_FLASHCARDS = listOf(
        Pair("Ohm's Law", "V = I · R (Voltage equals Current times Resistance at constant temperature)"),
        Pair("Lens Formula", "1/v - 1/u = 1/f (with Cartesian sign convention)"),
        Pair("Mirror Formula", "1/v + 1/u = 1/f"),
        Pair("Refractive Index (n)", "n = c / v (Ratio of speed of light in vacuum to speed in medium)"),
        Pair("Quadratic Formula", "x = [-b ± √(b² - 4ac)] / (2a)"),
        Pair("Photosynthesis Equation", "6CO₂ + 6H₂O + Sunlight → C₆H₁₂O₆ + 6O₂"),
        Pair("Pythagoras Theorem", "Hypotenuse² = Base² + Perpendicular² (a² + b² = c²)"),
        Pair("Joule's Heating Law", "H = I² · R · t")
    )

    val DAILY_QUOTES = listOf(
        "\"The beautiful thing about learning is that no one can take it away from you.\" — B.B. King",
        "\"Education is the most powerful weapon which you can use to change the world.\" — Nelson Mandela",
        "\"Success is the sum of small efforts, repeated day in and day out.\" — Robert Collier",
        "\"Study hard, for the well is deep, and our brains are shallow.\" — Richard Baxter",
        "\"Don't let what you cannot do interfere with what you can do.\" — John Wooden"
    )
}
