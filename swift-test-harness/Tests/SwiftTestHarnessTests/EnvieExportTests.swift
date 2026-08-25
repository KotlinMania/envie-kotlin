import Testing
import Envie

@Suite("Envie Export Tests")
struct EnvieExportTests {
    @Test("swift module imports cleanly")
    func swiftModuleLoads() throws {
        #expect(Bool(true))
    }
}
