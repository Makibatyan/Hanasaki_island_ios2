//
//  KeyboardViewController.swift
//  なまりめ
//
//  Created by k25122kk on 2026/08/29.
//

import UIKit
import shared // KMP shared モジュール

class KeyboardViewController: UIInputViewController {

    @IBOutlet var nextKeyboardButton: UIButton!

    private let flickMap: [String: [String]] = [
        "あ": ["あ", "い", "う", "え", "お"],
        "か": ["か", "き", "く", "け", "こ"],
        "さ": ["さ", "し", "す", "せ", "そ"],
        "た": ["た", "ち", "つ", "て", "と"],
        "な": ["な", "に", "ぬ", "ね", "の"],
        "は": ["は", "ひ", "ふ", "へ", "ほ"],
        "ま": ["ま", "み", "む", "め", "も"],
        "や": ["や", "（", "ゆ", "）", "よ"],
        "ら": ["ら", "り", "る", "れ", "ろ"],
        "わ": ["わ", "を", "ん", "ー", "〜"],
        "、、。?!": ["、", "。", "？", "！", "・"]
    ]

    private var currentMarkedText: String = ""
    private var candidateScrollView: UIScrollView!
    private var candidateStackView: UIStackView!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = UIColor(red: 0.12, green: 0.12, blue: 0.13, alpha: 1.0)
        
        let containerStack = UIStackView()
        containerStack.axis = .vertical
        containerStack.spacing = 6
        containerStack.translatesAutoresizingMaskIntoConstraints = false
        self.view.addSubview(containerStack)
        
        // 1. 上部：予測変換バー
        let topCandidateContainer = UIStackView()
        topCandidateContainer.axis = .horizontal
        topCandidateContainer.alignment = .center
        topCandidateContainer.spacing = 8
        topCandidateContainer.heightAnchor.constraint(equalToConstant: 42).isActive = true
        
        candidateScrollView = UIScrollView()
        candidateScrollView.showsHorizontalScrollIndicator = false
        
        candidateStackView = UIStackView()
        candidateStackView.axis = .horizontal
        candidateStackView.spacing = 20
        candidateStackView.alignment = .center
        candidateStackView.translatesAutoresizingMaskIntoConstraints = false
        candidateScrollView.addSubview(candidateStackView)
        
        NSLayoutConstraint.activate([
            candidateStackView.topAnchor.constraint(equalTo: candidateScrollView.topAnchor),
            candidateStackView.bottomAnchor.constraint(equalTo: candidateScrollView.bottomAnchor),
            candidateStackView.leadingAnchor.constraint(equalTo: candidateScrollView.leadingAnchor, constant: 16),
            candidateStackView.trailingAnchor.constraint(equalTo: candidateScrollView.trailingAnchor, constant: -16),
            candidateStackView.heightAnchor.constraint(equalTo: candidateScrollView.heightAnchor)
        ])
        
        let dropdownBtn = UIButton(type: .system)
        dropdownBtn.setTitle("∨", for: .normal)
        dropdownBtn.setTitleColor(.white, for: .normal)
        dropdownBtn.titleLabel?.font = UIFont.systemFont(ofSize: 18, weight: .medium)
        dropdownBtn.widthAnchor.constraint(equalToConstant: 36).isActive = true
        
        topCandidateContainer.addArrangedSubview(candidateScrollView)
        topCandidateContainer.addArrangedSubview(dropdownBtn)
        
        containerStack.addArrangedSubview(topCandidateContainer)
        
        // 2. キーボード本体
        let gridStack = UIStackView()
        gridStack.axis = .horizontal
        gridStack.spacing = 6
        gridStack.distribution = .fillEqually
        
        gridStack.addArrangedSubview(createColumn(titles: ["→", "↺", "ABC", "😀"], isSpecial: true))
        gridStack.addArrangedSubview(createColumn(titles: ["あ", "た", "ま", "小 ゛゜"]))
        gridStack.addArrangedSubview(createColumn(titles: ["か", "な", "や", "わ"]))
        gridStack.addArrangedSubview(createColumn(titles: ["さ", "は", "ら", "、、。?!"]))
        gridStack.addArrangedSubview(createRightColumn())
        
        containerStack.addArrangedSubview(gridStack)
        
        // 地球儀ボタン
        self.nextKeyboardButton = UIButton(type: .system)
        self.nextKeyboardButton.setTitle("🌐", for: [])
        self.nextKeyboardButton.titleLabel?.font = UIFont.systemFont(ofSize: 22)
        self.nextKeyboardButton.setTitleColor(.white, for: .normal)
        self.nextKeyboardButton.translatesAutoresizingMaskIntoConstraints = false
        self.nextKeyboardButton.addTarget(self, action: #selector(handleInputModeList(from:with:)), for: .allTouchEvents)
        self.view.addSubview(self.nextKeyboardButton)
        
        // キーボード高さ設定（260ptに拡張）
        let heightConstraint = self.view.heightAnchor.constraint(equalToConstant: 260)
        heightConstraint.priority = .defaultHigh
        
        NSLayoutConstraint.activate([
            heightConstraint,
            containerStack.topAnchor.constraint(equalTo: self.view.topAnchor, constant: 4),
            containerStack.leftAnchor.constraint(equalTo: self.view.leftAnchor, constant: 4),
            containerStack.rightAnchor.constraint(equalTo: self.view.rightAnchor, constant: -4),
            containerStack.bottomAnchor.constraint(equalTo: self.view.bottomAnchor, constant: -4),
            
            self.nextKeyboardButton.leftAnchor.constraint(equalTo: self.view.leftAnchor, constant: 12),
            self.nextKeyboardButton.bottomAnchor.constraint(equalTo: self.view.bottomAnchor, constant: -4)
        ])
    }

    private func createColumn(titles: [String], isSpecial: Bool = false) -> UIStackView {
        let col = UIStackView()
        col.axis = .vertical
        col.distribution = .fillEqually
        col.spacing = 6
        
        for title in titles {
            let btn = createButton(title: title, isSpecial: isSpecial)
            col.addArrangedSubview(btn)
        }
        return col
    }

    private func createRightColumn() -> UIStackView {
        let col = UIStackView()
        col.axis = .vertical
        col.distribution = .fill
        col.spacing = 6
        
        let delBtn = createButton(title: "⌫", isSpecial: true)
        let nextBtn = createButton(title: "次候補", isSpecial: true)
        let confirmBtn = createButton(title: "確定", isSpecial: true)
        confirmBtn.backgroundColor = UIColor(white: 0.28, alpha: 1.0)
        
        col.addArrangedSubview(delBtn)
        col.addArrangedSubview(nextBtn)
        col.addArrangedSubview(confirmBtn)
        
        delBtn.heightAnchor.constraint(equalTo: nextBtn.heightAnchor).isActive = true
        confirmBtn.heightAnchor.constraint(equalTo: delBtn.heightAnchor, multiplier: 2.0).isActive = true
        
        return col
    }

    private func createButton(title: String, isSpecial: Bool) -> UIButton {
        let btn = UIButton(type: .system)
        btn.setTitle(title, for: .normal)
        btn.titleLabel?.font = UIFont.systemFont(ofSize: 18, weight: .regular)
        btn.setTitleColor(.white, for: .normal)
        btn.backgroundColor = isSpecial ? UIColor(white: 0.22, alpha: 1.0) : UIColor(white: 0.32, alpha: 1.0)
        btn.layer.cornerRadius = 5
        
        if flickMap[title] != nil {
            let pan = UIPanGestureRecognizer(target: self, action: #selector(handleFlick(_:)))
            btn.addGestureRecognizer(pan)
        }
        
        btn.addTarget(self, action: #selector(handleTap(_:)), for: .touchUpInside)
        return btn
    }

    @objc private func handleTap(_ sender: UIButton) {
        guard let title = sender.title(for: .normal) else { return }
        
        switch title {
        case "⌫":
            if !currentMarkedText.isEmpty {
                currentMarkedText.removeLast()
                updateMarkedText()
            } else {
                textDocumentProxy.deleteBackward()
            }
        case "確定":
            confirmCurrentText()
        case "小 ゛゜":
            toggleDakutenOrSmallChar()
        case "次候補", "→", "↺", "ABC", "😀":
            break
        default:
            if let chars = flickMap[title] {
                appendCharacter(chars[0])
            }
        }
    }

    @objc private func handleFlick(_ gesture: UIPanGestureRecognizer) {
        guard let btn = gesture.view as? UIButton,
              let title = btn.title(for: .normal),
              let chars = flickMap[title] else { return }
        
        if gesture.state == .ended {
            let translation = gesture.translation(in: btn)
            let x = translation.x
            let y = translation.y
            
            var index = 0
            if y < -20 && abs(x) < abs(y) { index = 2 }
            else if x < -20 && abs(y) < abs(x) { index = 1 }
            else if x > 20 && abs(y) < abs(x) { index = 3 }
            else if y > 20 && abs(x) < abs(y) { index = 4 }
            
            if index < chars.count {
                appendCharacter(chars[index])
            }
        }
    }

    private func appendCharacter(_ char: String) {
        currentMarkedText += char
        updateMarkedText()
    }

    private func toggleDakutenOrSmallChar() {
        guard !currentMarkedText.isEmpty else { return }
        let lastChar = String(currentMarkedText.suffix(1))
        
        let dakutenMap: [String: String] = [
            "か": "が", "が": "か", "き": "ぎ", "ぎ": "き", "く": "ぐ", "ぐ": "く", "け": "げ", "げ": "け", "こ": "ご", "ご": "こ",
            "さ": "ざ", "ざ": "さ", "し": "じ", "じ": "し", "す": "ず", "ず": "す", "せ": "ぜ", "ぜ": "せ", "そ": "ぞ", "ぞ": "そ",
            "た": "だ", "だ": "た", "ち": "ぢ", "ぢ": "ち", "つ": "づ", "づ": "っ", "っ": "つ", "て": "で", "で": "て", "と": "ど", "ど": "と",
            "は": "ば", "ば": "ぱ", "ぱ": "は", "ひ": "び", "び": "ぴ", "ぴ": "ひ", "ふ": "ぶ", "ぶ": "ぷ", "ぷ": "ふ", "へ": "べ", "べ": "ぺ", "ぺ": "へ", "ほ": "ぼ", "ぼ": "ぽ", "ぽ": "ほ",
            "あ": "ぁ", "ぁ": "あ", "い": "ぃ", "ぃ": "い", "う": "ぅ", "ぅ": "う", "え": "ぇ", "ぇ": "え", "お": "ぉ", "ぉ": "お",
            "や": "ゃ", "ゃ": "や", "ゆ": "ゅ", "ゅ": "ゆ", "よ": "ょ", "ょ": "よ", "わ": "ゎ", "ゎ": "わ"
        ]
        
        if let converted = dakutenMap[lastChar] {
            currentMarkedText.removeLast()
            currentMarkedText.append(converted)
            updateMarkedText()
        }
    }

    private func updateMarkedText() {
        if !currentMarkedText.isEmpty {
            textDocumentProxy.setMarkedText(currentMarkedText, selectedRange: NSRange(location: currentMarkedText.count, length: 0))
        } else {
            textDocumentProxy.setMarkedText("", selectedRange: NSRange(location: 0, length: 0))
        }
        updateCandidateBar()
    }

    private func confirmCurrentText() {
        guard !currentMarkedText.isEmpty else { return }
        textDocumentProxy.insertText(currentMarkedText)
        currentMarkedText = ""
        textDocumentProxy.setMarkedText("", selectedRange: NSRange(location: 0, length: 0))
        updateCandidateBar()
    }

    private func updateCandidateBar() {
        candidateStackView.arrangedSubviews.forEach { $0.removeFromSuperview() }
        
        let query = currentMarkedText
        var candidates: [String] = []
        
        if !query.isEmpty {
            candidates = [query, query + "〜"]
            
            /*
            do {
                let dict = CojadsDictionary()
                // candidates = dict.search(query: query)
            } catch {
                print("Dictionary error: \(error)")
            }
            */
        }
        
        for candidate in candidates {
            let btn = UIButton(type: .system)
            btn.setTitle(candidate, for: .normal)
            btn.setTitleColor(.white, for: .normal)
            btn.titleLabel?.font = UIFont.systemFont(ofSize: 20, weight: .regular)
            btn.addTarget(self, action: #selector(candidateSelected(_:)), for: .touchUpInside)
            candidateStackView.addArrangedSubview(btn)
        }
    }

    @objc private func candidateSelected(_ sender: UIButton) {
        guard let text = sender.title(for: .normal) else { return }
        textDocumentProxy.insertText(text)
        currentMarkedText = ""
        textDocumentProxy.setMarkedText("", selectedRange: NSRange(location: 0, length: 0))
        updateCandidateBar()
    }

    override func viewWillLayoutSubviews() {
        self.nextKeyboardButton.isHidden = !self.needsInputModeSwitchKey
        super.viewWillLayoutSubviews()
    }
}
