// Inject the build-time process.env into the webpack bundle produced by karma-webpack.
// This is required for browser targets where `process` is not defined at runtime:
// webpack's DefinePlugin rewrites `process.env[name]` to a static object lookup,
// eliminating the ReferenceError and making environment variables (e.g. PATH) available.
config.webpack = config.webpack || {};
config.webpack.plugins = (config.webpack.plugins || []).concat([
    new (require('webpack').DefinePlugin)({
        'process.env': JSON.stringify(process.env),
    }),
]);
