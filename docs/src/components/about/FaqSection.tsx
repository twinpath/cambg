import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"

const FAQ_ITEMS = [
  {
    question: "What Android versions are supported?",
    answer:
      "CamBG Record requires Android 7.0 (Nougat, API level 24) or higher. The application is compiled against SDK 36 and targets SDK 35 for maximum compatibility with modern Android security policies.",
  },
  {
    question: "Does the app record when the screen is off?",
    answer:
      "Yes. CamBG Record utilizes a persistent foreground service with camera, microphone, and media projection types to maintain recording continuity even when the device display is off or the application is moved to the background.",
  },
  {
    question: "How does motion detection work?",
    answer:
      "The motion detection engine uses frame-differencing analysis to compare consecutive video frames. Sensitivity is adjustable across Low, Medium, and High levels. When movement is detected, the event is logged with a timestamp and confidence metric in the chronological event feed.",
  },
  {
    question: "What about battery consumption?",
    answer:
      "CamBG Record includes an option to request battery optimization exemptions from the Android system. This prevents the OS from interrupting the recording service during long-duration sessions. Battery impact varies based on resolution, frame rate, and detection settings.",
  },
  {
    question: "Is my data private?",
    answer:
      "All recorded video files are stored locally on your device. The application does not upload recordings to any remote server. Network access is used only for optional Firebase AI features and app update checks.",
  },
  {
    question: "Can I configure video quality?",
    answer:
      "Yes. CamBG Record offers configurable output resolution (including 1080p default), adjustable frame rate (default 30 fps), selectable bitrate presets (High, Medium, Low), and audio source configuration with stereo or mono channel modes.",
  },
  {
    question: "How do I install the APK?",
    answer:
      "Download the APK from the Download page, enable 'Install Unknown Apps' in your device security settings for your browser or file manager, then open the APK file to begin installation. On first launch, grant Camera, Microphone, Storage, and Notification permissions.",
  },
  {
    question: "Is the source code available?",
    answer:
      "Yes. CamBG Record is hosted on GitHub at github.com/twinpath/cambg. The project uses a proprietary license -- all rights are reserved by the project owner.",
  },
] as const

export function FaqSection() {
  return (
    <section id="faq" className="px-4 py-16">
      <div className="mx-auto max-w-3xl">
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          <h2 className="font-heading text-2xl font-bold tracking-tight">
            Frequently Asked Questions
          </h2>
          <p className="max-w-lg text-sm text-muted-foreground">
            Common questions about CamBG Record, its features, and how to get
            started.
          </p>
        </div>

        <Accordion type="single" collapsible>
          {FAQ_ITEMS.map((item) => (
            <AccordionItem key={item.question} value={item.question}>
              <AccordionTrigger>{item.question}</AccordionTrigger>
              <AccordionContent>
                <p>{item.answer}</p>
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      </div>
    </section>
  )
}
