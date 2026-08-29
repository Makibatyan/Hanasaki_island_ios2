import SwiftUI
import shared

struct ContentView: View {
    @State private var testText: String = ""
    
    var body: some View {
        VStack(spacing: 0) {
            // ヘッダーバー
            HStack {
                Text("方言キーボード (COJADS)")
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                Spacer()
            }
            .padding()
            .background(Color.blue)
            
            ScrollView {
                VStack(spacing: 24) {
                    // タイトル
                    Text("方言キーボード 設定")
                        .font(.title)
                        .fontWeight(.bold)
                        .padding(.top, 20)
                    
                    Text("方言キーボードを使用するには、以下の2つのステップを設定してください。")
                        .font(.body)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)
                    
                    // ボタン群
                    VStack(spacing: 16) {
                        Button(action: {
                            if let url = URL(string: UIApplication.openSettingsURLString) {
                                UIApplication.shared.open(url)
                            }
                        }) {
                            Text("1. キーボードを有効にする")
                                .font(.headline)
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.blue)
                                .cornerRadius(4)
                        }
                        
                        Button(action: {
                            if let url = URL(string: UIApplication.openSettingsURLString) {
                                UIApplication.shared.open(url)
                            }
                        }) {
                            Text("2. 方言キーボードを選択する")
                                .font(.headline)
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.blue)
                                .cornerRadius(4)
                        }
                    }
                    .padding(.horizontal)
                    
                    // 入力テストエリア
                    VStack(alignment: .leading, spacing: 8) {
                        Text("入力テスト")
                            .font(.headline)
                            .foregroundColor(.secondary)
                        
                        TextField("ここをタップして入力テストができます", text: $testText)
                            .padding()
                            .background(Color.white)
                            .cornerRadius(4)
                            .shadow(color: Color.black.opacity(0.05), radius: 2, x: 0, y: 1)
                    }
                    .padding(.horizontal)
                    .padding(.top, 10)
                    
                    Spacer()
                }
            }
            .background(Color(UIColor.systemGroupedBackground))
        }
        .edgesIgnoringSafeArea(.top)
    }
}
