import SwiftUI

struct ContentView: View {
    @State private var inputText: String = ""

    var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()

            VStack(spacing: 24) {
                Text("方言キーボード 設定")
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                    .padding(.top, 40)

                Text("方言キーボードを使用するには、以下の2つのステップを設定してください。")
                    .font(.subheadline)
                    .foregroundColor(Color.gray)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 20)

                VStack(spacing: 12) {
                    Button(action: {
                        if let url = URL(string: UIApplication.openSettingsURLString) {
                            UIApplication.shared.open(url)
                        }
                    }) {
                        Text("1. キーボードを有効にする")
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(Color.blue)
                            .cornerRadius(8)
                    }

                    Button(action: {}) {
                        Text("2. 方言キーボードを選択する")
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(Color.blue)
                            .cornerRadius(8)
                    }
                }
                .padding(.horizontal, 20)

                VStack(alignment: .leading, spacing: 8) {
                    Text("入力テスト")
                        .font(.subheadline)
                        .foregroundColor(.white)

                    // 入力文字を「黒色」に明示指定
                    TextField("ここをタップして入力テストができます", text: $inputText)
                        .font(.system(size: 16))
                        .foregroundColor(.black)
                        .padding(.horizontal, 12)
                        .frame(height: 50)
                        .background(Color.white)
                        .cornerRadius(6)
                        .accentColor(.blue)
                }
                .padding(.horizontal, 20)

                Spacer()
            }
        }
    }
}

#Preview {
    ContentView()
}
