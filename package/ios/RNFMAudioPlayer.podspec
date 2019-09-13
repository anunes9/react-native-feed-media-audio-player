
Pod::Spec.new do |s|
  s.name         = "RNFMAudioPlayer"
  s.version      = "2.3.5"
  s.summary      = "RNFMAudioPlayer"
  s.description  = <<-DESC
                  RNFMAudioPlayer is the React Native wrapper for the Feed.fm Audio SDK
                   DESC
  s.homepage     = "https://feed.fm/"
  s.license      = { :type => "RESERVED", :file => "LICENSE.md" }
  s.author             = { "Eric Lambrecht" => "eric@feed.fm", "Arveen Kumar" => "arveen@feed.fm" }
  s.platform     = :ios, "8.0"
  s.source       = { :git => "https://github.com/feedfm/react-native-feed-media-audio-player", :tag => "master" }
  s.source_files  = "RNFMAudioPlayer/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  s.dependency "FeedMedia"
  #s.dependency "others"

end

  
